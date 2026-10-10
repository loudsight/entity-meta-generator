package com.loudsight.codemap.model;

import java.util.List;

public record ClassSignature(
        String packageName,
        String simpleName,
        String kind,
        String superclass,
        List<String> interfaces,
        List<String> classAnnotations,
        String javadocSummary,
        List<MethodSignature> methods,
        List<FieldSignature> constants) {

    public ClassSignature {
        interfaces = List.copyOf(interfaces);
        classAnnotations = List.copyOf(classAnnotations);
        methods = List.copyOf(methods);
        constants = List.copyOf(constants);
    }
}
