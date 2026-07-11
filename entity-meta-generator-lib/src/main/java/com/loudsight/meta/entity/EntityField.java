package com.loudsight.meta.entity;

import java.util.Collection;
import java.util.function.Function;

import com.loudsight.meta.annotation.Id;
import com.loudsight.meta.annotation.Transient;

/**
 * Record representing a field of an entity.
 * Delegates to SchemaField for shape data (isId, isTransient, name, isEnum, isCollection).
 * @param <E> the entity type
 * @param <T> the field type
 * @param schemaField the class-free field metadata
 * @param typeClass the field type class
 * @param annotations the annotations on this field
 * @param getter the getter function
 * @param setter the setter function
 */
public record EntityField<E, T> (
        SchemaField schemaField,
        Class<T> typeClass,
        Collection<EntityAnnotation> annotations,
        @Transient Function<E, T> getter,
        @Transient Setter<E, T> setter) {

    /**
     * Functional interface for setting field values.
     * @param <E> the entity type
     * @param <T> the field type
     */
    @FunctionalInterface
    public interface Setter<E, T> {
        /**
         * Sets the field value on an entity.
         * @param entity the entity
         * @param value the value to set
         */
        void apply(E entity, T value);
    }

    /**
     * Constructor for codegen compatibility - builds SchemaField from individual parameters.
     * This maintains the existing 7-arg public constructor signature so codegen needs no change.
     */
    public EntityField(String name,
                       Class<T> typeClass,
                       boolean isEnum,
                       boolean isCollection,
                       Collection<EntityAnnotation> annotations,
                       Function<E, T> getter,
                       Setter<E, T> setter) {
        this(
                new SchemaField(
                        name,
                        typeClass.getName(),
                        isEnum,
                        isCollection,
                        annotations.stream().anyMatch(it -> Id.class.getName().equals(it.getName())),
                        annotations.stream().anyMatch(it -> Transient.class.getName().equals(it.getName()))
                ),
                typeClass,
                annotations,
                getter,
                setter
        );
    }

    public EntityField(
            String name,
            Class<T> typeClass,
            boolean isEnum,
            boolean isCollection,
            Collection<EntityAnnotation> annotations
    ) {
        this(name, typeClass, isEnum, isCollection, annotations, it -> null, (e, v) -> {
        });
    }

    /**
     * Gets the field value from an entity.
     * @param entity the entity
     * @return the field value
     */
    public T get(E entity) {
        return getter.apply(entity);
    }

    /**
     * Sets the field value on an entity.
     * @param entity the entity
     * @param value the value to set
     */
    public void set(E entity, Object value) {
        setter.apply(entity, (T) value);
    }

    // Delegate methods to SchemaField
    public boolean isId() {
        return schemaField.isId();
    }

    public boolean isTransient() {
        return schemaField.isTransient();
    }

    public String name() {
        return schemaField.name();
    }

    public boolean isEnum() {
        return schemaField.isEnum();
    }

    public boolean isCollection() {
        return schemaField.isCollection();
    }
}