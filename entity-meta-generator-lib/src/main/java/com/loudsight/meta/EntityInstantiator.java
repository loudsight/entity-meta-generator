package com.loudsight.meta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityParameter;
import com.loudsight.meta.serialization.TypeConverters;

public class EntityInstantiator {

    private static class EntityInstantiatorHolder {
        private static final EntityInstantiator INSTANCE = new EntityInstantiator();
    }
    // global access point
    public static EntityInstantiator getInstance() {
        return EntityInstantiator.EntityInstantiatorHolder.INSTANCE;
    }
    private EntityInstantiator() {
    }

    private <T> Object[] getParameters(EntityConstructor constructor,
                                  Map<String, EntityField<T, ?>> fieldsMap,
                                  Map<String, ?> entityMap) {
        return constructor.getEntityParameters()
                .stream()
            .map(it -> {
                var fieldName = it.name();
//            var type = it.parameterType.qualifiedName!!
                var fieldValue = entityMap.get(fieldName);

                if (fieldValue == null) {
                    return null;
                }
                var field = fieldsMap.get(fieldName);
                if (field == null) {
                    return fieldValue;
                }

                Object convertedValue;
                if (field.isEnum() && fieldValue instanceof String enumName) {
                    convertedValue = convertEnumValue(field.typeClass(), enumName);
                } else {
                    convertedValue = TypeConverters.getInstance().convert(fieldValue, field.typeClass());
                }

                return convertedValue;
            })
            .toArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T invoke(Collection<EntityConstructor> constructors,
                        Map<String, EntityField<T, ?>> fieldsMap,
                        Map<String, ?> paramsMap) {
        int size;
        if (paramsMap.containsKey("__className__")) {
            size = paramsMap.size() - 1;
        } else {
            size = paramsMap.size();
        }

        // If paramsMap is empty, try no-arg constructor directly
        if (size == 0) {
            var c = constructors
                    .stream()
                    .filter(it -> it.getEntityParameters().isEmpty())
                    .toList();

            if (!c.isEmpty()) {
                return c.get(0).<T>newInstance();
            }
            throw new IllegalStateException("Cannot invoke any constructors with " + paramsMap);
        }

        // Try exact match first
        var c = findConstructors(constructors, paramsMap, size, true);
        if (!c.isEmpty()) {
            return instantiateWithConstructor(c.get(0), fieldsMap, paramsMap);
        }

        // Fallback: try constructors matching subset of parameters
        c = findConstructors(constructors, paramsMap, size, false);
        if (!c.isEmpty()) {
            return instantiateWithConstructor(c.get(0), fieldsMap, paramsMap);
        }

        // Try no-arg constructor
        c = constructors
                .stream()
                .filter(it -> it.getEntityParameters().isEmpty())
                .toList();

        if (c.isEmpty()) {
            throw new IllegalStateException("Cannot invoke any constructors with " + paramsMap);
        }

        var entity = c.get(0).<T>newInstance();

        paramsMap
                .entrySet()
                .stream()
                .filter(e -> fieldsMap.containsKey(e.getKey()))
                .forEach(e -> {
                    var fieldName = e.getKey();
                    var fieldValue = e.getValue();
                    var field = fieldsMap.get(fieldName);
                    Object convertedValue;
                    if (field.isEnum() && fieldValue instanceof String enumName) {
                        convertedValue = convertEnumValue(field.typeClass(), enumName);
                    } else {
                        convertedValue = TypeConverters.getInstance().convert(fieldValue, field.typeClass());
                    }
                    field.set(entity, convertedValue);
                });
        return entity;
    }

    private <T> void setExtraFields(T entity, EntityConstructor constructor,
                                     Map<String, EntityField<T, ?>> fieldsMap,
                                     Map<String, ?> paramsMap) {
        paramsMap.entrySet().stream()
                .filter(e -> fieldsMap.containsKey(e.getKey()) && fieldsMap.get(e.getKey()).setter() != null)
                .filter(e -> !constructor.getEntityParameters().stream().map(EntityParameter::name).toList().contains(e.getKey()))
                .forEach(e -> {
                    var field = fieldsMap.get(e.getKey());
                    Object convertedValue;
                    if (field.isEnum() && e.getValue() instanceof String enumName) {
                        convertedValue = convertEnumValue(field.typeClass(), enumName);
                    } else {
                        convertedValue = TypeConverters.getInstance().convert(e.getValue(), field.typeClass());
                    }
                    field.set(entity, convertedValue);
                });
    }

    /**
     * Converts a wire-degraded enum name String back to a real enum instance. Throws a clear,
     * immediate exception if the enum type isn't resolvable on this JVM, rather than letting a
     * NullPointerException surface later and further from the actual cause (or worse, silently
     * dropping the field - setExtraFields only sets fields it successfully converts).
     */
    private Object convertEnumValue(Class<?> enumType, String enumName) {
        var meta = MetaRepository.getInstance().getMeta(enumType);
        if (meta == null) {
            throw new IllegalStateException("Cannot resolve Meta for enum type " + enumType.getName()
                    + " to convert value \"" + enumName + "\" - the enum type is not on this JVM's classpath");
        }
        return meta.newInstance(Map.of("name", enumName));
    }

    private List<EntityConstructor> findConstructors(Collection<EntityConstructor> constructors,
                                                      Map<String, ?> paramsMap, int size, boolean exactMatch) {
        return constructors
                .stream()
                .filter(it -> !it.getEntityParameters().isEmpty() && paramsMap.keySet().containsAll(it.getEntityParameters().stream().map(EntityParameter::name).toList()))
                .filter(it -> exactMatch ? it.getEntityParameters().size() >= size : it.getEntityParameters().size() < size)
                .sorted((a, b) -> Integer.compare(b.getEntityParameters().size(), a.getEntityParameters().size()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateWithConstructor(EntityConstructor constructor,
                                              Map<String, EntityField<T, ?>> fieldsMap,
                                              Map<String, ?> paramsMap) {
        var parameters = getParameters(constructor, fieldsMap, paramsMap);
        var entity = constructor.<T>newInstance(parameters);
        setExtraFields(entity, constructor, fieldsMap, paramsMap);
        return entity;
    }
}
