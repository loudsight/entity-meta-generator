package com.loudsight.meta.entity

class EntityParameter(
    val name: String,
    val parameterType: Class<*>,
    val annotations: List<EntityAnnotation>
) {
    constructor(
        name: String,
        parameterType: Class<*>,
    ) : this(name, parameterType, listOf())
}