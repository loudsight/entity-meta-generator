package com.loudsight.meta.generation;
//
//import com.loudsight.collection.MultiKeyMap;
//import com.loudsight.meta.*;
//import com.loudsight.meta.entity.*;
//import com.loudsight.useful.helper.JvmClassHelper;
//
//import java.lang.reflect.InvocationTargetException;
//import java.time.temporal.Temporal;
//import java.util.*;
//import javax.lang.model.element.TypeElement;
//
public class GeneratedMeta /*: MetaInfo*/ {
//
//    String typeName;
//    String simpleTypeName;
//    Boolean isEnum;
//    List<EntityTypeInfo> fields;
//    Collection<EntityMethodInfo> methods;
//    List<EntityConstructorInfo> constructors;
//    List<EntityAnnotationInfo> annotations;
//    List<EntityTypeInfo> classHierarchy;
//
//    public GeneratedMeta(
//            String typeName,
//            String simpleTypeName,
//            Boolean isEnum,
//            List<EntityTypeInfo> fields,
//            Collection<EntityMethodInfo> methods,
//            List<EntityConstructorInfo> constructors,
//            List<EntityAnnotationInfo> annotations,
//            List<EntityTypeInfo> classHierarchy
//    ) {
//        this.typeName = typeName;
//        this.simpleTypeName = simpleTypeName;
//        this.isEnum = isEnum;
//        this.fields = fields;
//        this.constructors = constructors;
//        this.annotations = annotations;
//        this.classHierarchy = classHierarchy;
//        this.methods = methods;
//    }
//
////    override fun <T : Any> getRelationships(meta: MetaInfo, entity: T): Map<String?, Any?> {
////        return meta.fields
////            .asSequence()
////            .filter {
////                it.annotations.stream().noneMatch { an: EntityAnnotationInfo -> an.name == Transient.class.name }
////            }
////            .filter { Objects.nonNull(it[entity]) }
////            .filter {
////                val value = it[entity]
////                value !is Collection<*> || value.size > 0
////            }
////            .filter { helper.isRelationship(it.typeName.java) }
////            .associateBy(
////                { it.name }
////            ) { it[entity] }
////    }
//
//    //   companion object {
//// override fun newInstance(fieldMap: Map<String, Any>): T {
////     TODO()
////            val className = fieldMap["__className__"]
////
////            if (className == null) {
//////                val newEntityMap = HashMap<String, Any>()
//////                entityMap.forEach {
//////                    val value = it.value
//////                    if (value is List<*>) {
//////                        val values = ArrayList<Any>()
//////                        newEntityMap[it.key] = values
//////
//////                        value.forEach { element ->
//////                            if (element is Map<*, *>) {
//////                                values.add(fromMap(Cast.uncheckedCast(element))!!)
//////                            }
//////                        }
//////                    } else {
//////                        newEntityMap[it.key] = it.value!!
//////                    }
//////                }
//////
//////                return Cast.uncheckedCast(newEntityMap)
////            }
////
////            val metaOpt = helper.getMeta<T>(className as String)
////
////            if (metaOpt.isPresent) {
////                val meta = metaOpt.get()
////                if (meta.typeClass.java.isEnum) {
////                    return meta.typeClass.java.getMethod("valueOf", String.class).invoke(null, fieldMap["name"]) as T
////                }
////                try {
////                    val result = meta.newInstance()
////
////                    meta.fields
////                        .filter { Objects.nonNull(fieldMap[it.name]) }
////                        .filter { field ->
////                            fieldMap.containsKey(field.name)
////                        }
////                        .forEach { field ->
////                            var value = fieldMap[field.name];
////
////                            if (value is Map<*, *> && value.containsKey("__className__")) {
////                                value = newInstance(ClassHelper.uncheckedCast(value))
////                            } else if (value is Collection<*>) {
////                                value = value.map {
////                                    if (it is Map<*, *>) {
////                                        newInstance(ClassHelper.uncheckedCast(it))
////                                    } else {
////                                        it
////                                    }
////                                }
////                                value = when (value) {
////                                    is Set<*> -> {
////                                        value.toSet()
////                                    }
////                                    is List<*> -> {
////                                        value.toList()
////                                    }
////                                    else -> {
////                                        value.toList()
////                                    }
////                                }
////                            }
////
////                            if (value is Map<*, *> && value.containsKey("__className__")) {
////                                value = helper.toMap(ClassHelper.uncheckedCast(value))
////                            }
////                            value = convert(value!!, field.typeClass.java)
////                            field.set(result, ClassHelper.uncheckedCast(value));
////                        }
////                    return result as T;
////                } catch (e: Exception) {
////                    // failed to directly set the fields
////                    // will try via constructors
////                }
////
////                return meta.constructors
////                    .asSequence()
////                    .filter { it.entityParameters.size >= (fieldMap.size - 1) }
////                    .sortedBy { it.entityParameters.size }
////                    .map { ConstructorMatcher<T>(it, fieldMap) }
////                    .filter { it.canInvoke }
////                    .first()
////                    .invoke()
////            }
////
////            throw RuntimeException("Failed to create entity")
////        }
//
//    private static MultiKeyMap<Class<?>, Class<?>, EntityHelper.Converter<Object, ?>> converters = EntityHelper.converters();
//
//
//    private static <K, T> T convert(K value, Class<?> typeClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        if (typeClass.isEnum()) {
//            return (T) typeClass.getDeclaredMethod("valueOf", String.class).invoke(null, value);
//        }
//        var function = converters.get(value.getClass(), typeClass);
//        if (function == null) {
//            return null;
//        }
//        return (T) function.apply(value);
//    }
//
//
//    static abstract class GeneratedMetaHelper {
//        abstract Optional<MetaInfo> getMeta(TypeElement classOrName);
//
////            private fun <T> toMapOf(entity: Collection<T>): Map<String, Any> {
//////            if (entity == null) {
//////                return emptyMap<String, Any>()
//////            }
////                val entities = HashMap<String, Any>()
////                var i = 0
////                entity.forEach {
////                    entities[i.toString()] = toMap(it)
////                    i++
////                }
////
////                return entities
////            }
//
////            fun toMap(entity: Any?): Map<String, Any> {
////                if (entity == null) {
////                    return mapOf()
////                }
////                if (entity is Collection<*>) {
////                    return toMapOf(entity)
////                }
////
////                return toMapOf(entity)
////            }
//
////            private fun <T: Any> toMapOf(entity: T): Map<String, Any> {
////    //            if (entity == null) {
////    //                return mapOf<String, Any>()
////    //            }
////                if (entity is Collection<*>) {
////                    return toMapOf(entity)
////                }
////
////                val meta = MetaRepository.getMeta(entity::class as KClass<T>)
////
////                val fields = mutableListOf(*meta!!.fields.toTypedArray())
////                fields.add(
////                    EntityFieldInfo(
////                        name = "__className__",
////                        String::class,
////                        isEnum = false,
////                        isCollection = false,
////                        listOf(),
////                        { meta.typeName },
////                        { _: Any, _ ->  }
////                    )
////                )
////
////                return fields
////                    .filter {
////                        it.annotations.none { an: EntityAnnotationInfo -> an.name == Transient.class.name }
////                    }
////                    .filter { Objects.nonNull(it.get(entity)) }
////                    .associateBy(
////                        EntityFieldInfo::name
////                    ) {
////                        when {
////                            it.isEnum -> {
////                                (it[ClassHelper.uncheckedCast(entity)] as Enum<*>).name
////                            }
////                            isRelationship(it.typeClass.java) -> toMap(
////                                it[ClassHelper.uncheckedCast(entity)]
////                            )
////                            else -> it[ClassHelper.uncheckedCast(entity)]
////                        } as Any
////                    }
////            }
//
//        public static boolean isRelationship(Class<?> aClass) {
//            if (aClass == null) {
//                return true;
//            } else if (Class.class.isAssignableFrom(aClass)) {
//                return false;
//            }
//            if (JvmClassHelper.isPrimitive(aClass) ||
//                    Temporal.class.isAssignableFrom(aClass) ||
//                    Enum.class.isAssignableFrom(aClass)
//            ) {
//                return false;
//            }
//            var isCollection = Collection.class.isAssignableFrom(aClass);
//
//            return isCollection || !aClass.getName().startsWith("java");
//        }
//    }
}