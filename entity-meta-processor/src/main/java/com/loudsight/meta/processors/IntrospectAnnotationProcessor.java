package com.loudsight.meta.processors;

import com.loudsight.meta.exceptions.ClassGenerationException;
import com.loudsight.meta.writter.MetaClassWriter;
import com.loudsight.meta.writter.SchemaClassWriter;
import com.loudsight.meta.EntityMetaProcessor;
import com.loudsight.meta.MetaInfo;
import com.loudsight.meta.Schema;
import com.loudsight.meta.entity.SchemaField;
import com.loudsight.meta.annotation.Introspect;
import com.loudsight.meta.exceptions.BadAnnotationUsageException;
import com.loudsight.meta.model.SchemaSourceCodeGenerator;

import com.google.auto.service.AutoService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.loudsight.meta.annotation.Introspect")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@AutoService(Processor.class)
public final class IntrospectAnnotationProcessor
        extends AbstractAnnotationProcessor<Introspect, MetaInfo> {
    private static final String ANNOTATION_NAME = Introspect.class.getSimpleName();
    private static final String INDEX_FILE = "META-INF/introspect-types.index";
    /**
     * One line per introspected type:
     * {@code <fully-qualified type name> TAB <Schema#fingerprint()> TAB <field names>}, sorted by
     * type name, UTF-8, '\n'-terminated. The fingerprint is the value the generated
     * {@code <X>Schema} yields at runtime, so a build can be compared with stored fingerprints
     * before it is deployed; the field names (see {@link #fieldNames}) let it be compared with
     * stored property keys too.
     */
    static final String SCHEMAS_FILE = "META-INF/introspect-schemas.tsv";
    private final Set<String> introspectedTypes = new HashSet<>();
    private final Map<String, Schema> schemaByType = new TreeMap<>();

    public IntrospectAnnotationProcessor() {
        super(Introspect.class);
     }

    @Override
    void validateElement(Element annotatedElement) throws BadAnnotationUsageException {
        // Accept both regular classes and records
        boolean isClassOrRecord = annotatedElement.getKind().isClass() || 
                                  "RECORD".equals(annotatedElement.getKind().toString());
        if (!isClassOrRecord) {
            throw new BadAnnotationUsageException(annotatedElement.getSimpleName().toString(),
                    ANNOTATION_NAME,
                    "Only classes and records may use this annotation");
        } else {
            if (annotatedElement.getModifiers().contains(Modifier.ABSTRACT)) {
                throw new BadAnnotationUsageException(annotatedElement.getSimpleName().toString(),
                        ANNOTATION_NAME,
                        "Only non-abstract classes are supported at this time");
            }
        }
    }

    @Override
    MetaInfo transformElementToModel(Element annotatedElement, Introspect annotation) {
        String typeName = ((TypeElement) annotatedElement).getQualifiedName().toString();
        introspectedTypes.add(typeName);

        EntityMetaProcessor entityMetaProcessor = new EntityMetaProcessor(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils()
        );
        return entityMetaProcessor.transformElementToModel(annotatedElement, annotation);
    }

    @Override
    @SuppressFBWarnings(value = "REC_CATCH_EXCEPTION",
            justification = "Deliberate: any failure during meta/schema generation - checked "
                    + "(IOException from the writers) or runtime - is wrapped as a "
                    + "ClassGenerationException diagnostic so a generation bug surfaces as a "
                    + "compiler error rather than crashing javac with a stack trace.")
    void finalizeElementProcessing(MetaInfo model)  throws ClassGenerationException {
        try {
            MetaClassWriter writer = new MetaClassWriter(this.processingEnv.getFiler());
            writer.generateMetaClass(model);
            SchemaClassWriter schemaWriter = new SchemaClassWriter(this.processingEnv.getFiler());
            schemaWriter.generateSchemaClass(model);
            schemaByType.put(model.typeName(), new SchemaSourceCodeGenerator(model).toSchema());
            EntityMetaProcessor entityMetaProcessor = new EntityMetaProcessor(
                    processingEnv.getTypeUtils(),
                    processingEnv.getElementUtils()
            );
            entityMetaProcessor.finalizeElementProcessing(model);
        } catch (Exception e) {
            throw new ClassGenerationException(e);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        super.process(annotations, roundEnv);

        // Write index file after processing is complete
        if (roundEnv.processingOver()) {
            try {
                var resource = processingEnv.getFiler().createResource(
                        javax.tools.StandardLocation.CLASS_OUTPUT,
                        "",
                        INDEX_FILE
                );
                try (BufferedWriter writer = new BufferedWriter(resource.openWriter())) {
                    for (String typeName : introspectedTypes) {
                        writer.write(typeName);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(
                        javax.tools.Diagnostic.Kind.ERROR,
                        "Failed to write introspect types index: " + e.getMessage()
                );
            }
            writeSchemaFingerprints();
        }

        // Never claim @Introspect: other processors (and future rounds) must still see it.
        // AbstractAnnotationProcessor.process() already returns false unconditionally, so this
        // is the same value it always produced, just stated where the contract is visible.
        return false;
    }

    private void writeSchemaFingerprints() {
        try {
            var resource = processingEnv.getFiler().createResource(
                    javax.tools.StandardLocation.CLASS_OUTPUT,
                    "",
                    SCHEMAS_FILE
            );
            try (Writer writer = resource.openWriter()) {
                writer.write(formatSchemaFingerprints(schemaByType));
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    javax.tools.Diagnostic.Kind.ERROR,
                    "Failed to write introspect schema fingerprints: " + e.getMessage()
            );
        }
    }

    static String formatSchemaFingerprints(Map<String, Schema> schemaByType) {
        var out = new StringBuilder();
        new TreeMap<>(schemaByType).forEach((typeName, schema) ->
                out.append(typeName).append('\t').append(schema.fingerprint())
                        .append('\t').append(fieldNames(schema)).append('\n'));
        return out.toString();
    }

    /**
     * The third column: the schema's field names ({@link SchemaField#name()} of every
     * {@link Schema#fields()} entry, the names a persistence server logs as a label's deployed
     * fields), sorted, comma-joined, empty for a type with no fields. A field name is a Java
     * identifier, so it never contains a comma or a tab.
     */
    static String fieldNames(Schema schema) {
        return schema.fields().stream()
                .map(SchemaField::name)
                .sorted()
                .collect(Collectors.joining(","));
    }
}
