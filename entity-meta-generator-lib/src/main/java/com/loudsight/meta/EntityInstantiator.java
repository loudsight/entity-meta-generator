package com.loudsight.meta;

import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityParameter;
import com.loudsight.meta.serialization.TypeConverters;

import java.util.Collection;
import java.util.Map;

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
                if (field.isEnum() && fieldValue instanceof String) {
                    var meta = MetaRepository.getInstance().getMeta(field.getTypeClass());
                            convertedValue = meta.newInstance(Map.of("name", fieldValue));
                } else {
                    convertedValue = TypeConverters.getInstance().convert(fieldValue, field.getTypeClass());
                }
//
                return convertedValue;
//            } else {
//                type = type.replace("<out ", "<")
////                var e = EntityTransforms.getEntityType(it.parameterType.java, value)
//                if (type.startsWith("kotlin.Array<")) {
//                    value = helper.eval(type.replace("kotlin.Array", "arrayOf") + "()")
//                    return@map value
//                } else if (type.startsWith("kotlin.collections.List<")) {
//                    var expr = type.replace("kotlin.collections.List", "arrayListOf") + "()"
//                    expr = expr.replace("<T", "<*")
//                    value = helper.eval(expr)
//                    return@map value
//                } else if (type.startsWith("kotlin.collections.Collection<")) {
//                    var expr = type.replace("kotlin.collections.Collection", "arrayListOf") + "()"
//                    expr = expr.replace("<T", "<*")
//                    value = helper.eval(expr)
//                    return@map value
//                }
//                return@map value
//            }
            })
            .toArray();
    }

    public <T> T invoke(Collection<EntityConstructor> constructors,
                        Map<String, EntityField<T, ?>> fieldsMap,
                        Map<String, ?> paramsMap) {
        int size;
        if (paramsMap.containsKey("__className__")) {
            size = paramsMap.size() - 1;
        } else {
            size = paramsMap.size();
        }

        var c = constructors
                .stream()
                .filter(it -> !it.getEntityParameters().isEmpty() && paramsMap.keySet().containsAll(it.getEntityParameters().stream().map(EntityParameter::name).toList()))
                .filter(it -> it.getEntityParameters().size() >= size)
                .sorted((a, b) -> Integer.compare(b.getEntityParameters().size(), a.getEntityParameters().size()))
                .toList();

        if (!c.isEmpty()) {
            var it = c.get(0);
            var parameters = getParameters(it, fieldsMap, paramsMap);
            return it.newInstance(parameters);
        }

        c = constructors
                .stream()
                .filter(it -> it.getEntityParameters().isEmpty())
                .toList();
//                .forEach(it -> {
        var entity = c.get(0).<T>newInstance();

        if (c.isEmpty()) {
            throw new IllegalStateException("Cannot invoke any constructors with " + paramsMap);
        }

        paramsMap
                .entrySet()
                .stream()
                .filter(e -> fieldsMap.containsKey(e.getKey()))
                .forEach(e -> {
                    var fieldName = e.getKey();
                    var fieldValue = e.getValue();
                    var field = fieldsMap.get(fieldName);
                    Object convertedValue;
                    if (field.isEnum() && fieldValue instanceof String) {
                        var meta = MetaRepository.getInstance().getMeta(field.getTypeClass());
                        convertedValue = meta.newInstance(Map.of("name", fieldValue));
                    } else {
                        convertedValue = TypeConverters.getInstance().convert(fieldValue, field.getTypeClass());
                    }
                    field.set(entity, convertedValue);
                });
        return entity;
    }

    public void x() {

//                var parameters = meta.constructors
//                    .first { it.entityParameters.isNotEmpty() }CollectionEntityTransform
//                    .entityParameters.map { parameter ->
//                    var name = parameter.name
//                    var entity = entityMap[name]
//
//                    if (entity is Map<*, *>) {
//                        entity = fromXXX(Cast.uncheckedCast(entity))
//                    } else if (entity is List<*>) {
//                        entity = (entity as List<*>).map {
//                            if (it is Map<*, *>) {
//                                fromXXX(Cast.uncheckedCast(it))
//                            } else {
//                                it
//                            }
//                        }.toList()
//                    }
//                    var value = entity
//                    if (value == null) {
//                        parameter.nullValue
//                    } else if (KClass::class.java.isAssignableFrom(parameter.parameterType.java)) {
//                        if (value is String) {
//                            Class.forName(value).kotlin
//                        } else {
//                            null
//                        }
//                    } else if (parameter.parameterType.java.isAssignableFrom(value.javaClass)) {
//                        value
//                    } else {
//                        null
//                    }

    }

//    private fun constructInstanceFromValue(value: Any?): Any? {
//        if (value is List<*>) {
//            return value.map { constructInstanceFromValue(it) }.toList()
//        } else if (value is Map<*, *> && value.containsKey("__className__")) {
//            var meta = helper.getMeta<Any>(JvmClassHelper.classForName<Any>(value["__className__"] as String))
//
//            if (meta.isPresent) {
//                return meta.get().newInstance(ClassHelper.uncheckedCast(value))
//            }
//            // value = metaRepository.fromMap(value)
//        }
//        return value
//    }
}
