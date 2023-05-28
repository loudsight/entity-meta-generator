package com.loudsight.meta

import com.loudsight.meta.entity.EntityConstructor
import com.loudsight.meta.entity.EntityField
import com.loudsight.meta.serialization.TypeConverters
import com.loudsight.useful.helper.ClassHelper
import kotlin.reflect.KClass

object EntityInstantiator {

    private fun <T> getParameters(constructor: EntityConstructor,
                                  fieldsMap: Map<String, EntityField<T, *>>,
                                  entityMap: Map<String, Any>): Array<Any?> {
        return constructor.entityParameters
            .map {
                val fieldName = it.name
//            var type = it.parameterType.qualifiedName!!
                val fieldValue = entityMap[fieldName]

                if (fieldValue == null) {
                    return@map null
                }
                val field = fieldsMap[fieldName]
                if (field == null) {
                    return@map fieldValue
                }

                val convertedValue = if (field.isEnum && fieldValue is String) {
                    val meta = MetaRepository.getMeta(field.typeClass)!!
                    meta.newInstance(mapOf(kotlin.Pair("name", fieldValue)))
                } else {
                    TypeConverters.convert(fieldValue, field.typeClass.kotlin as KClass<Any>)!!
                }
//
                return@map convertedValue
//            } else {
//                type = type.replace("<out ", "<")
////                val e = EntityTransforms.getEntityType(it.parameterType.java, value)
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
            }
            .toTypedArray()
    }

    fun <T> invoke(constructors: Collection<EntityConstructor>,
                   fieldsMap: Map<String, EntityField<T, *>>,
                   paramsMap: Map<String, Any>): T {
        val size =
            if (paramsMap.containsKey("__className__")) {
                paramsMap.size - 1
            } else {
                paramsMap.size
            }

        val c = constructors
            .asSequence()
            .filter { it.entityParameters.isNotEmpty() && paramsMap.keys.containsAll(it.entityParameters.map { k -> k.name }.toList()) }
            .filter { it.entityParameters.size >= size }
            .sortedByDescending { it.entityParameters.size }
            .toList()

        c.forEach {
                val parameters = getParameters(it, fieldsMap, paramsMap)
                return@invoke it.newInstance(*parameters)
            }

        constructors
            .asSequence()
            .filter { it.entityParameters.isEmpty() }
            .first {
                val entity = it.newInstance<T>()

                paramsMap
                    .filter { (fieldName, _) -> fieldsMap.containsKey(fieldName) }
                    .forEach { (fieldName, fieldValue) ->
                    val field = fieldsMap[fieldName]!!
                    val convertedValue = if (field.isEnum && fieldValue is String) {
                        val meta = MetaRepository.getMeta(field.typeClass)!!
                        meta.newInstance(mapOf(kotlin.Pair("name", fieldValue)))
                    } else {
                        TypeConverters.convert(fieldValue, field.typeClass.kotlin )!!
                    }
                    field[entity] = convertedValue
                }
                return@invoke entity
            }

        throw IllegalStateException("Cannot invoke any constructors with $paramsMap")
    }

    fun x() {

//                val parameters = meta.constructors
//                    .first { it.entityParameters.isNotEmpty() }
//                    .entityParameters.map { parameter ->
//                    val name = parameter.name
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
//                    val value = entity
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
//            val meta = helper.getMeta<Any>(JvmClassHelper.classForName<Any>(value["__className__"] as String))
//
//            if (meta.isPresent) {
//                return meta.get().newInstance(ClassHelper.Companion.uncheckedCast(value))
//            }
//            // value = metaRepository.fromMap(value)
//        }
//        return value
//    }
}
