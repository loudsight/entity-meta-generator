package com.loudsight.meta.entity

class EntityConstructor(val entityParameters: List<EntityParameter>, private val constructor: (Array<out Any?>) -> Any) {
    fun <T> newInstance(vararg parameters: Any?): T {
        return constructor(parameters) as T
    }
}