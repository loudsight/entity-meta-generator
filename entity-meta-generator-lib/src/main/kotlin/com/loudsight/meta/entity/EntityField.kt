package com.loudsight.meta.entity

import com.loudsight.meta.annotation.Id
import com.loudsight.meta.annotation.Transient

class EntityField<E, T: Any>(
    @field:Id val name: String,
    val typeClass: Class<T>,
    val isEnum: Boolean,
    val isCollection: Boolean,
    val annotations: Collection<EntityAnnotation>,
    @field:Transient val getter: (entity: E) -> T?,
    @field:Transient val setter: (entity: E, value: T) -> Unit
) {
    @field:Transient
    var isId: Boolean = annotations.any { it.name == "com.loudsight.meta.annotation.Id" }
    @field:Transient
    var isTransient: Boolean = annotations.any { it.name == "com.loudsight.meta.annotation.Transient" }

    constructor(
        name: String,
        typeClass: Class<T>,
        isEnum: Boolean = false,
        isCollection: Boolean = false,
        annotations: List<EntityAnnotation>
    ) : this(name,
        typeClass,
        isEnum,
        isCollection,
        annotations,
         { null },
         { _: E, _: T -> })

    operator fun get(entity: E): T? {
        return getter.invoke(entity)
    }

    operator fun set(entity: E, value: Any) {
        setter.invoke(entity, value as T)
    }
}