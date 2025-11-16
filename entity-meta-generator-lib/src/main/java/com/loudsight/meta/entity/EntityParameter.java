package com.loudsight.meta.entity;

import java.util.List;

/**
 * Record representing a parameter of a method.
 * @param name the parameter name
 * @param parameterType the parameter type class
 * @param annotations the annotations on this parameter
 */
public record EntityParameter(
        String name,
        Class<?> parameterType,
        List<EntityAnnotation> annotations
) {
    /**
     * Constructs an EntityParameter with default empty annotations.
     * @param name the parameter name
     * @param parameterType the parameter type class
     */
    public EntityParameter(
            String name,
            Class<?> parameterType
    ) {
        this(name, parameterType, List.of());
    }
}