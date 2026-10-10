package com.loudsight.meta.entity;

/**
 * Class-free field metadata for Schema.
 * Contains only String/boolean data, no Class<?> references.
 * Used by persistence-server to understand field shapes without loading business entity classes.
 */
public record SchemaField(
    String name,
    String typeName,
    boolean isEnum,
    boolean isCollection,
    boolean isId,
    boolean isTransient
) {
}
