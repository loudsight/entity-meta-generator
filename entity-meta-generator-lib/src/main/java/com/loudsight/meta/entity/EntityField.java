package com.loudsight.meta.entity;

import java.util.Collection;
import java.util.function.Function;

import com.loudsight.meta.annotation.Id;
import com.loudsight.meta.annotation.Transient;

/**
 * Record representing a field of an entity.
 * @param <E> the entity type
 * @param <T> the field type
 * @param isId whether this is an ID field
 * @param isTransient whether this field is transient
 * @param name the field name
 * @param typeClass the field type class
 * @param isEnum whether the field type is an enum
 * @param isCollection whether the field type is a collection
 * @param annotations the annotations on this field
 * @param getter the getter function
 * @param setter the setter function
 */
public record EntityField<E, T> (
        @Transient boolean isId,
        @Transient boolean isTransient,
        @Id String name,
        Class<T> typeClass,
        boolean isEnum,
        boolean isCollection,
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

    public EntityField(String name,
                       Class<T> typeClass,
                       boolean isEnum,
                       boolean isCollection,
                       Collection<EntityAnnotation> annotations,
                       Function<E, T> getter,
                       Setter<E, T> setter) {
        this(
                annotations.stream().anyMatch(it -> "com.loudsight.meta.annotation.Id".equals(it.getName())),
                annotations.stream().anyMatch(it -> "com.loudsight.meta.annotation.Transient".equals(it.getName())),
                name,
                typeClass,
                isEnum,
                isCollection,
                annotations, getter,
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
}