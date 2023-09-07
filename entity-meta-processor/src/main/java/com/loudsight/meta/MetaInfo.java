package com.loudsight.meta;

import com.loudsight.meta.entity.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaInfo {

    private final String typeName;
    private final String simpleTypeName;
    private final boolean isEnum;
    private final boolean isRecord;
    private final List<EntityVariableInfo> fields;
    private final  List<EntityConstructorInfo> constructors;
    private final List<EntityAnnotationInfo> annotations;
    private final List<EntityTypeInfo> typeHierarchy;
//    private final List<EntityMethodInfo> methods;
    private final Map<String, EntityMethodInfo> methodMap;

    public MetaInfo (String typeName,
              String simpleTypeName,
              boolean isEnum,
              boolean isRecord,
                     List<EntityVariableInfo> fields,
              List<EntityConstructorInfo> constructors,
              List<EntityAnnotationInfo> annotations,
              List<EntityTypeInfo> typeHierarchy,
              List<EntityMethodInfo> methods) {
        this.typeName = typeName;
        this.simpleTypeName = simpleTypeName;
        this.isEnum = isEnum;
        this.isRecord = isRecord;
        this.fields = fields;
        this.constructors = constructors;
        this.annotations = annotations;
        this.typeHierarchy = typeHierarchy;
//        this.methods = methods;
        methodMap =
//        get() {
//            val sortedEntityMethodInfos: List<EntityMethodInfo> = ArrayList(EntityMethodInfos)
//                .sortedBy { obj: EntityMethodInfo -> obj.name }
                methods.stream().collect(Collectors.toMap(EntityMethodInfo::name, it -> it));
    }
    public String getPackageName() {
        var lastDotIndex = typeName.lastIndexOf('.');
        return typeName.substring(0, lastDotIndex);
    }

    public String typeName() {
        return typeName;
    }
    public List<EntityVariableInfo> fields() {
        return fields;
    }

    public List<EntityVariableInfo> getSortedEntityFieldInfos() {
        return fields
                .stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    }

    private Map<String, EntityVariableInfo> getFieldMap() {
        return fields.stream().collect(
                Collectors.toMap(EntityVariableInfo::getName, it -> it)
        );
    }

    EntityVariableInfo getFieldByName(String name) {
        return getFieldMap().get(name);
    }

    public Map<String, EntityMethodInfo> getMethodMap() {
        return methodMap;
    }

    public String simpleTypeName() {
        return simpleTypeName;
    }

    public boolean isEnum() {
        return isEnum;
    }
    public boolean isRecord() {
        return isRecord;
    }

    public List<EntityConstructorInfo> constructors() {
        return constructors;
    }

    public List<EntityAnnotationInfo> annotations() {
        return annotations;
    }

    public List<EntityTypeInfo> typeHierarchy() {
        return typeHierarchy;
    }
//
//    fun getMethod(methodName: String): EntityMethodInfo {
//        return methodMap[methodName]!!
//    }
//
//    fun <T : Any> getRelationships(meta: MetaInfo, entity: T): Map<String?, Any?> {
//        TODO("Not yet implemented")
//    }

}