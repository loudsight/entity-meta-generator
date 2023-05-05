package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object ByteEntityTransform : EntityTransform<Byte>(EntityType.BYTE, Byte::class) {

    override fun serializeEntity(entity: Byte, bytes: MutableList<Byte>) {
        bytes.add(EntityType.BYTE.code)
        bytes.add(entity)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Byte {
        return bytes.next()
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is Byte
    }
}