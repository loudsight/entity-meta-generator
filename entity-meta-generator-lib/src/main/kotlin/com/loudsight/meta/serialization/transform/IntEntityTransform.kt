package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object IntEntityTransform : EntityTransform<Int>(EntityType.INTEGER, Int::class) {

    override fun serializeEntity(entity: Int, bytes: MutableList<Byte>) {
        bytes.add(EntityType.INTEGER.code)
        writeInt(entity, bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Int {
        return readInt(bytes)
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is Int
    }
}