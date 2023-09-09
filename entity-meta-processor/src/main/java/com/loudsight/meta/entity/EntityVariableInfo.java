package com.loudsight.meta.entity;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

public class EntityVariableInfo {
    private final String name;
    private final EntityTypeInfo type;
    private final TypeElement typeElement;
    private final boolean isEnum;
    private final boolean isCollection;
    private final Collection<EntityAnnotationInfo> annotations;

    public EntityVariableInfo(
            String name,
            EntityTypeInfo type,
            TypeElement typeElement,
            boolean isEnum,
            boolean isCollection,
            Collection<EntityAnnotationInfo> annotations
    ) {
        this.name = name;
        this.type = type;
        this.typeElement = typeElement;
        this.isEnum = isEnum;
        this.isCollection = isCollection;
        this.annotations = annotations;
    }
//    public Boolean isNullable() {
//
//        return annotations.stream().anyMatch(it -> {
//            return it.getName().equals(Nullable.class.getName());
//        });
//    }

    public String getName() {
        return name;
    }

    public EntityTypeInfo getType() {
        return type;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public Collection<EntityAnnotationInfo> getAnnotations() {
        return annotations;
    }
}