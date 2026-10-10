package com.loudsight.meta.entity;

import javax.lang.model.type.TypeMirror;

public class PrimitiveEntityTypeInfo extends EntityTypeInfo {
    public PrimitiveEntityTypeInfo(String typeName, TypeMirror type) {
        super(typeName, false, type);
    }
}
