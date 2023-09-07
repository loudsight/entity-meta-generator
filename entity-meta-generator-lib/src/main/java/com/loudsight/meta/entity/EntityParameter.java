package com.loudsight.meta.entity;

import java.util.List;

public record EntityParameter(
        String name,
        Class<?> parameterType,
        List<EntityAnnotation> annotations
) {
    public EntityParameter(
            String name,
            Class<?> parameterType
    ) {
        this(name, parameterType, List.of());
    }
}