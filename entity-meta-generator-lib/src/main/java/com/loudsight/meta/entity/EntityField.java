package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Id;
import com.loudsight.meta.annotation.Transient;

import java.util.Collection;
import java.util.function.Function;

public class EntityField<E, T> {
    @Transient
    private final boolean isId;

    @Transient
    private final boolean isTransient;
    @Id
    private final String name;

    private final Class<T> typeClass;
    private final boolean isEnum;
    private final boolean isCollection;
    private final Collection<EntityAnnotation> annotations;
    @Transient
    private final Function<E, T> getter;
    @Transient
    private final Setter<E, T> setter;

    public EntityField(String name,
            Class<T> typeClass,
            boolean isEnum,
            boolean isCollection,
            Collection<EntityAnnotation> annotations,
            Function<E, T> getter,
                Setter<E, T> setter) {
        this.name = name;
        this.typeClass = typeClass;
        this.isEnum = isEnum;
        this.isCollection = isCollection;
        this.getter = getter;
        this.setter = setter;
        this.annotations = annotations;
        this.isId = annotations.stream().anyMatch(it -> "com.loudsight.meta.annotation.Id".equals(it.getName()));
        this.isTransient = annotations.stream().anyMatch(it -> "com.loudsight.meta.annotation.Transient".equals(it.getName()));
    }

    public EntityField(
            String name,
            Class<T> typeClass,
            boolean isEnum,
            boolean isCollection,
            Collection<EntityAnnotation> annotations
    ) {
        this(name, typeClass, isEnum, isCollection, annotations, it -> null, (e, v) -> {});
    }

    public T get(E entity) {
        return getter.apply(entity);
    }

    public void set(E entity, Object value) {
        setter.apply(entity, (T)value);
    }
    public String getName() {
        return name;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public boolean isId() {
        return isId;
    }

    public Collection<EntityAnnotation> getAnnotations() {
        return annotations;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public interface Setter<E, T> {
        void apply(E entity, T value);
    }
}