package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Id;
import com.loudsight.meta.annotation.Transient;

import java.util.Collection;
import java.util.function.Function;

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


    public interface Setter<E, T> {
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

    public T get(E entity) {
        return getter.apply(entity);
    }

    public void set(E entity, Object value) {
        setter.apply(entity, (T) value);
    }
}