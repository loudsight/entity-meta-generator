package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityTransforms
import com.loudsight.meta.serialization.EntityType
import kotlin.reflect.KClass

abstract class CollectionEntityTransform<T: Collection<*>>(entityType: EntityType, entityClass: KClass<T>)
    : EntityTransform<T>(entityType, entityClass) {

    abstract fun newCollection(size: Int, init: (index: Int) -> Any?): T

    override fun serializeEntity(entity: T, bytes: MutableList<Byte>) {
        bytes.add(entityType.code)
        writeInt(entity.size, bytes)

        entity.forEach {
            if (it == null) {
                bytes.add(EntityType.NULL.code)
            } else {
                val entityTransform: EntityTransform<Any>? = EntityTransforms.getEntityTransform<Any>(it)
                entityTransform?.serializeEntity(it, bytes)
            }
        }
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): T {
        val size = readInt(bytes)

        val collection = newCollection(size)
        {
            val entityType = EntityType.getEntityType(bytes.next().toInt().toChar())
            val transform = EntityTransforms.getEntityTransform<Any>(entityType)
            transform?.deserializeEntity(bytes)
        }

        return collection
    }
}