package com.loudsight.meta.entity;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale;

public class GenericEntityTypeInfo extends EntityTypeInfo {

    private final List<EntityTypeInfo> genericArguments;
    private final String str;

    public GenericEntityTypeInfo(String typeName, List<EntityTypeInfo> genericArguments, TypeMirror type) {
        super(typeName, true, type);

        this.genericArguments = List.copyOf(genericArguments);
        var genericArgumentsStr =

                genericArguments.stream().map(EntityTypeInfo::toString).collect(Collectors.joining(", "));
        str = String.format(Locale.ROOT, "%s<%s>", typeName, genericArgumentsStr);
    }


    public List<EntityTypeInfo> getGenericArguments() {
        return genericArguments;
    }

    @Override
    public String toString() {
        return str;
    }
}
