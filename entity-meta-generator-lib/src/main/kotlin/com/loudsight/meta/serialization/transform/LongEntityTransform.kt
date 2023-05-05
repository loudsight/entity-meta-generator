package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object LongEntityTransform : EntityTransform<Long>(EntityType.LONG, Long::class) {

    override fun serializeEntity(entity: Long, bytes: MutableList<Byte>) {
        bytes.add(EntityType.LONG.code)
        writeLong(entity, bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Long {
        return readLong(bytes)
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is Long
    }

}