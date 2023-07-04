package com.loudsight.meta;

import com.loudsight.meta.entity.EntityAnnotation;
import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityMethod;

import java.util.*;
import java.util.stream.Collectors;

public abstract class DefaultMeta<T> implements Meta<T> {

    private final String typeName;
    private final String packageName;
    private final String simpleTypeName;
    private final Class<T> typeClass;
    private final List<EntityConstructor> constructors;
    private final List<EntityAnnotation> annotations;
    private final List<Class<?>> typeHierarchy;
    private final List<EntityMethod<T, ?>> methods;

    private final Map<String, EntityField<T, ?>> fieldMap;


    public DefaultMeta(
            String packageName,
            String simpleTypeName,
            Class<T> typeClass,
            List<EntityField<T, ?>> fields,
            List<EntityConstructor> constructors,
            List<EntityAnnotation> annotations,
            List<Class<?>> typeHierarchy,
            List<EntityMethod<T, ?>> methods
    ) {
        this.typeName = packageName + "."+ simpleTypeName;
        this.packageName = packageName;
        this.simpleTypeName = simpleTypeName;
        this.typeClass = typeClass;
        this.constructors = constructors;
        this.annotations = annotations;
        this.typeHierarchy = typeHierarchy;
        this.methods = methods;

        var fieldMap = new TreeMap<String, EntityField<T, ?>>(String::compareTo);
        fields.forEach(it -> fieldMap.put(it.getName(), it));

        this.fieldMap = Collections.unmodifiableMap(fieldMap);
    }

    //    private val methodMap: Map<String, EntityMethod<T, *>>
//        get() {
////            val sortedEntityMethods: List<EntityMethod<T, *>> = ArrayList(entityMethods)
////                .sortedBy { obj: EntityMethod<T, *> -> obj.name }
//
//            return methods.associateBy({ obj: EntityMethod<T, *> -> obj.name }, { it })
//        }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    @Override
    public Class<T> getTypeClass() {
        return typeClass;
    }

    @Override
    public Collection<EntityField<T, ?>> getFields() {
        return fieldMap.values();
    }

    @Override
    public List<EntityConstructor> getConstructors() {
        return constructors;
    }

    @Override
    public List<EntityAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public T newInstance() {
        return newInstance(Collections.emptyMap());
    }

    @Override
    public final Map<String, EntityField<T, ?>> getFieldAsMap() {
        return fieldMap;
    }

    @Override
    public final EntityField<T, ?> getFieldByName(String name) {
        return fieldMap.get(name);
    }
////
////    fun getMethod(methodName: String): EntityMethod<T, ?> {
////        return methodMap[methodName]!!
////    }
//
@Override
    public Map<String, Object> toMap(T entity) {
        return fieldMap
                .entrySet()
                .stream()
                .filter(it -> Objects.nonNull(it.getValue().get(entity)))
            .collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            it -> it.getValue().get(entity)
                    )
            );
    }

    @Override
    public List<Class<?>> getTypeHierarchy() {
        return typeHierarchy;
    }

    @Override
    public List<EntityMethod<T, ?>> getMethods() {
        return methods;
    }

//    fun <T : Object> getRelationships(meta: Meta<T>, entity: T): Map<String?, Object?> {
//        TODO("Not yet implemented")
//    }


}
