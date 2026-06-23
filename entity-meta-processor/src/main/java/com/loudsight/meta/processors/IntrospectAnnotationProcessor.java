package com.loudsight.meta.processors;

import com.loudsight.meta.exceptions.ClassGenerationException;
import com.loudsight.meta.model.descriptors.AttributeDescriptor;
import com.loudsight.meta.model.descriptors.ClassDescriptor;
import com.loudsight.meta.model.descriptors.MethodDescriptor;
import com.loudsight.meta.model.descriptors.TypeMapper;
import com.loudsight.meta.writter.MetaClassWriter;
import com.loudsight.meta.EntityMetaProcessor;
import com.loudsight.meta.MetaInfo;
import com.loudsight.meta.annotation.Introspect;
import com.loudsight.meta.exceptions.BadAnnotationUsageException;

import com.google.auto.service.AutoService;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@SupportedAnnotationTypes("com.loudsight.meta.annotation.Introspect")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@AutoService(Processor.class)
public final class IntrospectAnnotationProcessor
        extends AbstractAnnotationProcessor<Introspect, MetaInfo> {
    private static final String ANNOTATION_NAME = Introspect.class.getSimpleName();
    private static final String INDEX_FILE = "META-INF/introspect-types.index";
    private final Set<String> introspectedTypes = new HashSet<>();

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
        String typeName = annotatedElement.asType().toString();
        introspectedTypes.add(typeName);
        
        ClassDescriptor classDescriptor = annotatedElement.accept(new TypeMapper(), null).getClassDescriptor();
        Map<AttributeDescriptor, Optional<MethodDescriptor>> attributeSetterMapping = new HashMap<>();
        for (AttributeDescriptor attribute : classDescriptor.getAttributes()) {
            Optional<MethodDescriptor> setter = classDescriptor.getMethods().stream()
                    .filter(method ->
                    method.getName().toLowerCase().equals(String.format("set%s",attribute.name().toLowerCase())))
                    .findFirst();
            attributeSetterMapping.put(attribute, setter);
        }
        EntityMetaProcessor entityMetaProcessor = new EntityMetaProcessor(
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils()
        );
        return entityMetaProcessor.transformElementToModel(annotatedElement, annotation);
    }

    @Override
    void finalizeElementProcessing(MetaInfo model)  throws ClassGenerationException {
        try {
            MetaClassWriter writer = new MetaClassWriter(this.processingEnv.getFiler());
            writer.generateMetaClass(model);
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
        boolean result = super.process(annotations, roundEnv);
        
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
        }
        
        return result;
    }
}
