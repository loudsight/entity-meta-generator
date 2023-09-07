package com.loudsight.meta.entity;

import java.util.List;

public record EntityMethodInfo(
    String name,
    List<EntityVariableInfo> parameters,
    EntityTypeInfo returnType,
    List<EntityAnnotationInfo> annotations
){

}