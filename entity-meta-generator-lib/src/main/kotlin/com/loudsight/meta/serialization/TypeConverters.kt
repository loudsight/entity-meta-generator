package com.loudsight.meta.serialization

import com.loudsight.useful.helper.ClassHelper
import kotlin.reflect.KClass

object TypeConverters {
    private val converters = mutableMapOf<KClass<Any>, MutableMap<KClass<Any>, TypeConverter<*, *>>>()

    private val NO_CONVERTERS: MutableMap<KClass<Any>, TypeConverter<*, *>> = mutableMapOf()

    init {
        register(Int::class, Long::class, object : TypeConverter<Int, Long> {
            override fun convert(from: Long?): Int {
                return from!!.toInt()
            }
        })

    }

    fun <T : Any, F : Any> register(to: KClass<T>, from: KClass<F>, converter: TypeConverter<T, F>) {
        val typeConverters = converters.getOrPut(to as KClass<Any>) { mutableMapOf() }
        typeConverters[from as KClass<Any>] = converter
    }

    fun <T : Any> convert(from: Any, toType: KClass<T>): T? {
        if (toType == from::class) {
            return from as T
        }
        val fromConverter = converters.getOrElse(toType as KClass<Any>) { NO_CONVERTERS }
        val fromKClass = ClassHelper.uncheckedCast<KClass<Any>>(from::class)
        val toConverter = fromConverter.getOrElse(fromKClass) { NoOpConverter() } as TypeConverter<Any, Any>

        return toConverter.convert(from) as T
    }

    internal class NoOpConverter : TypeConverter<Any, Any> {
        override fun convert(from: Any?): Any? {
            return from
        }
    }
}
