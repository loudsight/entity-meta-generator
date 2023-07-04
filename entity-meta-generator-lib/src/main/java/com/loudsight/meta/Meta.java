package com.loudsight.meta;

import com.loudsight.meta.entity.EntityAnnotation;
import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Meta<T> {
    String getTypeName();

    String getPackageName();

    String getSimpleTypeName();

    Class<T> getTypeClass();

    Collection<EntityField<T, ?>> getFields();

    List<EntityConstructor> getConstructors();

    List<EntityAnnotation> getAnnotations();
    default T newInstance() {
        return newInstance(Collections.emptyMap());
    }

    abstract T newInstance(Map<String, ?> ggg);

    Map<String, EntityField<T, ?>> getFieldAsMap() ;

    EntityField<T, ?> getFieldByName(String name) ;
    ////
////    fun getMethod(methodName: String): EntityMethod<T, ?> {
////        return methodMap[methodName]!!
////    }
//
    List<Class<?>> getTypeHierarchy();

    List<EntityMethod<T, ?>> getMethods();
    Map<String, Object> toMap(T entity);

//    <T> Map<String, Object> getRelationships();
}