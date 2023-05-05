package com.loudsight.meta

import com.loudsight.useful.helper.JvmClassHelper.toClass
import com.loudsight.useful.helper.ClassHelper.Companion.uncheckedCast
import com.loudsight.meta.EntityHelper
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.lang.invoke.MethodHandles
import java.time.LocalDateTime
import java.util.function.Function
import kotlin.reflect.KClass

interface EntityHelper {
    companion object {

        val primitives = arrayListOf(
            Boolean::class,
            Integer::class,
            Double::class,
            Unit::class,
            Long::class,
            String::class,
            LocalDateTime::class
        )

        fun isPrimitive(entity: KClass<*>): Boolean {
            return primitives.contains(entity)
        }

        fun converters(): Map<Pair<Class<*>, Class<*>?>, Function<*, *>> {
            return java.util.Map.of<Pair<Class<*>, Class<*>?>, Function<*, *>>(
                Pair<Class<*>, Class<*>?>(Long::class.java, Int::class.javaPrimitiveType),
                Function<Long, Any> { obj: Long -> obj.toInt() } as Function<Long, *>
            )
        }

        fun <T: Any> convert(value: Any?, targetClass: KClass<T>?): T {
            return convert(
                value!!, toClass<T>(
                    targetClass!!
                )
            )
        }

        fun <T> convert(value: Any, targetClass: Class<T>): T {
            var result: Any? = value
            try {
                if (Enum::class.java.isAssignableFrom(targetClass)) {
                    result = targetClass.getMethod("valueOf", String::class.java).invoke(null, value.toString())
                } else if (Int::class.javaPrimitiveType!!.isAssignableFrom(targetClass)) {
                    result = Integer.valueOf(value.toString())
                } else if (Long::class.javaPrimitiveType!!.isAssignableFrom(targetClass)) {
                    result = java.lang.Long.valueOf(value.toString())
                }
            } catch (e: Exception) {
                logger.error("Unexpected error", e)
            }
            return uncheckedCast(result)
        }

        val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}