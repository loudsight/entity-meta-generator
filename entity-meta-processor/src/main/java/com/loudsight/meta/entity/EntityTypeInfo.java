package com.loudsight.meta.entity;

import javax.lang.model.type.TypeMirror;

public class EntityTypeInfo {
    private final String typeName;

    private final boolean isObject;
    private final boolean isBoolean;

    TypeMirror typeMirror;
    private final String str;


    public EntityTypeInfo(String typeName, TypeMirror typeMirror) { // for primitive types
        this(typeName, false, typeMirror);
    }

    public EntityTypeInfo(String typeName, boolean isObject, TypeMirror typeMirror) {
        this.typeName = typeName;
        this.isObject = isObject;
        this.typeMirror = typeMirror;
        this.str = typeName;
        this.isBoolean = "boolean".equalsIgnoreCase(typeName);
    }


    @Override
    public String toString() {
        return str;
    }
//
//    boolean isGeneric() {
//        return !genericArguments.isEmpty();
//    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isObject() {
        return isObject;
    }

    public TypeMirror getType() {
        return typeMirror;
    }

    public boolean isBoolean() {
        return isBoolean;
    }
}