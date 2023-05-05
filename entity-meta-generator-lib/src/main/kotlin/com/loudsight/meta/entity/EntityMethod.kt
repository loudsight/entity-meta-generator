package com.loudsight.meta.entity

import kotlin.reflect.KClass

class EntityMethod<T, R: Any>(
    val name: String,
    val parameters: List<EntityParameter>,
    val returnType: KClass<R>,
    val annotations: List<EntityAnnotation>,
    val invoker: Invoker<T, R>
) {
    interface Invoker<T, R> {
        fun apply(instance: T, vararg params: Any?): R?
    }

    operator fun invoke(instance: T, vararg params: Any?): R? {
        return invoker.apply(instance, *params)
    }
}