package com.loudsight.meta.entity;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

public class GenericEntityTypeInfo extends EntityTypeInfo {

    private final List<EntityTypeInfo> genericArguments;
    private final String str;

    public GenericEntityTypeInfo(String typeName, List<EntityTypeInfo> genericArguments, TypeMirror type) {
        super(typeName, true, type);

        this.genericArguments = genericArguments;
        var genericArgumentsStr =

                genericArguments.stream().map(EntityTypeInfo::toString).collect(Collectors.joining(", "));
        str = String.format("%s<%s>", typeName, genericArgumentsStr);
    }


    public List<EntityTypeInfo> getGenericArguments() {
        return genericArguments;
    }

    @Override
    public String toString() {
        return str;
    }
}
