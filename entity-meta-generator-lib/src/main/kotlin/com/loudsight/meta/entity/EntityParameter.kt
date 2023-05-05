package com.loudsight.meta.entity

import kotlin.reflect.KClass

class EntityParameter(
    val name: String,
    val parameterType: KClass<*>,
    val annotations: List<EntityAnnotation>
) {
    constructor(
        name: String,
        parameterType: KClass<*>,
    ) : this(name, parameterType, listOf())
}