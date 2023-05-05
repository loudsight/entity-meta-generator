package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType
import kotlin.reflect.KClass

object BooleanEntityTransform : EntityTransform<Boolean>(EntityType.BOOLEAN, Boolean::class) {

    override fun serializeEntity(entity: Boolean, bytes: MutableList<Byte>) {
        bytes.add(EntityType.BOOLEAN.code)
        if (entity) {
            bytes.add(1.toByte())
        } else {
            bytes.add(0.toByte())
        }
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Boolean {
        return 1 - bytes.next() == 0
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is Boolean
    }
}