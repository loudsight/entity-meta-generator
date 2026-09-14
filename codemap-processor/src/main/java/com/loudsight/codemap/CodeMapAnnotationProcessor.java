package com.loudsight.codemap;

import com.google.auto.service.AutoService;
import com.loudsight.codemap.model.ClassSignature;
import com.loudsight.codemap.model.FieldSignature;
import com.loudsight.codemap.model.MethodSignature;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Extracts a lightweight class/method/field signature for every top-level type seen during
 * compilation and writes them, module-wide, to {@code META-INF/codemap/signatures.json} in the
 * module's compiled output. This is a side effect of a normal {@code mvn compile}; it never
 * claims any annotation, so error-prone, nullaway, and entity-meta-processor still see every
 * element in the same round.
 *
 * <p>Only opted-in modules (see the parent's {@code codemap-processing} profile, activated by a
 * {@code .codemap-enabled} marker file) put this processor on their annotation-processor path.
 *
 * <p>Extraction failures are logged as compiler warnings, never errors: this processor runs
 * unconditionally over code nobody opted a specific class into, so a bug in it must never fail
 * anyone's build.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@AutoService(Processor.class)
public final class CodeMapAnnotationProcessor extends AbstractProcessor {

    private static final String OUTPUT_PATH = "META-INF/codemap/signatures.json";
    private static final Set<ElementKind> SUPPORTED_KINDS = Set.of(
            ElementKind.CLASS, ElementKind.INTERFACE, ElementKind.ENUM,
            ElementKind.RECORD, ElementKind.ANNOTATION_TYPE);

    private final List<ClassSignature> signatures = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element root : roundEnv.getRootElements()) {
            if (root instanceof TypeElement typeElement && SUPPORTED_KINDS.contains(typeElement.getKind())) {
                try {
                    signatures.add(toSignature(typeElement));
                } catch (RuntimeException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                            "codemap: failed to extract signature for "
                                    + typeElement.getQualifiedName() + ": " + e.getMessage());
                }
            }
        }

        if (roundEnv.processingOver()) {
            writeOutput();
        }
        return false;
    }

    private ClassSignature toSignature(TypeElement type) {
        Elements elements = processingEnv.getElementUtils();

        TypeMirror superclassMirror = type.getSuperclass();
        String superclass = superclassMirror.getKind() == TypeKind.NONE ? null : superclassMirror.toString();

        List<String> interfaces = type.getInterfaces().stream().map(TypeMirror::toString).toList();
        List<String> classAnnotations = type.getAnnotationMirrors().stream()
                .map(mirror -> mirror.getAnnotationType().toString())
                .toList();

        List<MethodSignature> methods = ElementFilter.methodsIn(type.getEnclosedElements()).stream()
                .filter(CodeMapAnnotationProcessor::isPublicOrProtected)
                .map(CodeMapAnnotationProcessor::toMethodSignature)
                .toList();

        List<FieldSignature> constants = ElementFilter.fieldsIn(type.getEnclosedElements()).stream()
                .filter(CodeMapAnnotationProcessor::isPublicStaticFinal)
                .map(CodeMapAnnotationProcessor::toFieldSignature)
                .toList();

        return new ClassSignature(
                elements.getPackageOf(type).getQualifiedName().toString(),
                type.getSimpleName().toString(),
                type.getKind().toString(),
                superclass,
                interfaces,
                classAnnotations,
                firstDocLine(elements.getDocComment(type)),
                methods,
                constants);
    }

    private static boolean isPublicOrProtected(ExecutableElement method) {
        return method.getModifiers().contains(Modifier.PUBLIC)
                || method.getModifiers().contains(Modifier.PROTECTED);
    }

    private static boolean isPublicStaticFinal(VariableElement field) {
        return field.getModifiers().contains(Modifier.PUBLIC)
                && field.getModifiers().contains(Modifier.STATIC)
                && field.getModifiers().contains(Modifier.FINAL);
    }

    private static MethodSignature toMethodSignature(ExecutableElement method) {
        List<String> paramTypes = method.getParameters().stream()
                .map(param -> param.asType().toString())
                .toList();
        List<String> modifiers = method.getModifiers().stream()
                .map(modifier -> modifier.toString().toLowerCase(java.util.Locale.ROOT))
                .toList();
        return new MethodSignature(
                method.getSimpleName().toString(),
                paramTypes,
                method.getReturnType().toString(),
                modifiers);
    }

    private static FieldSignature toFieldSignature(VariableElement field) {
        Object constantValue = field.getConstantValue();
        String value = constantValue == null
                ? null
                : constantValue instanceof String s ? "\"" + s + "\"" : String.valueOf(constantValue);
        return new FieldSignature(
                field.getSimpleName().toString(),
                field.asType().toString(),
                value);
    }

    private static String firstDocLine(String docComment) {
        if (docComment == null) {
            return null;
        }
        for (String line : docComment.split("\n", -1)) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty()) {
                return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
            }
        }
        return null;
    }

    private void writeOutput() {
        if (signatures.isEmpty()) {
            return;
        }
        try {
            FileObject resource = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_PATH);
            try (Writer writer = resource.openWriter()) {
                writer.write(JsonWriter.toJson(signatures));
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "codemap: failed to write " + OUTPUT_PATH + ": " + e.getMessage());
        }
    }
}
