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
     * Gets the class-free Schema for this type.
     * @return the Schema
     */
    Schema getSchema();

    /**
     * Gets the fully qualified type name.
     * @return the type name
     */
    default String getTypeName() {
        return getSchema().typeName();
    }

    /**
     * Gets the package name.
     * @return the package name
     */
    default String getPackageName() {
        return getSchema().packageName();
    }

    /**
     * Gets the simple type name (without package).
     * @return the simple type name
     */
    default String getSimpleTypeName() {
        return getSchema().simpleTypeName();
    }

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
     * <p>
     * Deliberately abstract, not a default derived from getSchema(): DefaultMeta (the only
     * implementor) already backs this with a precomputed TreeMap in O(1)/O(log n), and a prior
     * Schema-derived default here was both O(n^2) (rebuilding Schema's field map, then linearly
     * rescanning getFields() for every entry to translate SchemaField back to EntityField) and
     * fed Collectors.toMap a possibly-null value on any name mismatch, which throws
     * NullPointerException. Kept abstract so a future implementor is forced to provide its own
     * efficient lookup rather than silently inheriting that landmine.
     * @return map of fields by name
     */
    Map<String, EntityField<T, ?>> getFieldAsMap();

    /**
     * Gets a field by name.
     * @param name the field name
     * @return the field
     */
    EntityField<T, ?> getFieldByName(String name);

    /**
     * Gets all ID fields (fields annotated with @Id).
     * @return collection of ID fields
     */
    default Collection<EntityField<T, ?>> getIdFields() {
        return getFields().stream()
                .filter(EntityField::isId)
                .collect(java.util.stream.Collectors.toList());
    }
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
    default List<Class<?>> getTypeHierarchy() {
        return getSchema().typeHierarchy().stream()
                .map(typeName -> {
                    try {
                        return Class.forName(typeName);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(c -> c != null)
                .collect(java.util.stream.Collectors.toList());
    }

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