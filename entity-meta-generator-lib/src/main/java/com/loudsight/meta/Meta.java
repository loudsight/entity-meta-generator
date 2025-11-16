package com.loudsight.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.loudsight.meta.entity.EntityAnnotation;
import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityMethod;

/**
 * Interface for entity metadata and reflection.
 * @param <T> the entity type
 */
public interface Meta<T> {
    /**
     * Gets the fully qualified type name.
     * @return the type name
     */
    String getTypeName();

    /**
     * Gets the package name.
     * @return the package name
     */
    String getPackageName();

    /**
     * Gets the simple type name (without package).
     * @return the simple type name
     */
    String getSimpleTypeName();

    /**
     * Gets the type class.
     * @return the type class
     */
    Class<T> getTypeClass();

    /**
     * Gets all fields of this type.
     * @return collection of fields
     */
    Collection<EntityField<T, ?>> getFields();

    /**
     * Gets all constructors of this type.
     * @return list of constructors
     */
    List<EntityConstructor> getConstructors();

    /**
     * Gets all annotations on this type.
     * @return list of annotations
     */
    List<EntityAnnotation> getAnnotations();
    /**
     * Creates a new instance with default constructor.
     * @return new instance
     */
    default T newInstance() {
        return newInstance(Collections.emptyMap());
    }

    /**
     * Creates a new instance with the given parameters.
     * @param ggg the parameters map
     * @return new instance
     */
    abstract T newInstance(Map<String, ?> ggg);

    /**
     * Gets fields as a map keyed by field name.
     * @return map of fields by name
     */
    Map<String, EntityField<T, ?>> getFieldAsMap() ;

    /**
     * Gets a field by name.
     * @param name the field name
     * @return the field
     */
    EntityField<T, ?> getFieldByName(String name) ;
    //
    // TODO: Implement getMethod when needed
    // fun getMethod(methodName: String): EntityMethod<T, ?> {
    //     return methodMap[methodName]!!
    // }
    //
    /**
     * Gets the type hierarchy.
     * @return list of classes in the hierarchy
     */
    List<Class<?>> getTypeHierarchy();

    /**
     * Gets all methods of this type.
     * @return list of methods
     */
    List<EntityMethod<T, ?>> getMethods();
    /**
     * Converts an entity instance to a map.
     * @param entity the entity instance
     * @return map representation of the entity
     */
    Map<String, Object> toMap(T entity);

//    <T> Map<String, Object> getRelationships();
}