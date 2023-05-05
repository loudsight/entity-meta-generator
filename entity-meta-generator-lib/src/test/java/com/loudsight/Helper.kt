package com.loudsight

import kotlin.reflect.KClass

object Helper {

    @JvmStatic
    fun <T: Any> toKClass(aClass: Class<T>): KClass<T> {
        return aClass.kotlin
    }

    @JvmStatic
    fun <T> uncheckedCast(entity: Any): T {
        return entity as T
    }
}