package com.loudsight.codemap.model;

import java.util.List;

public record MethodSignature(
        String name,
        List<String> paramTypes,
        String returnType,
        List<String> modifiers) {

    public MethodSignature {
        paramTypes = List.copyOf(paramTypes);
        modifiers = List.copyOf(modifiers);
    }
}
