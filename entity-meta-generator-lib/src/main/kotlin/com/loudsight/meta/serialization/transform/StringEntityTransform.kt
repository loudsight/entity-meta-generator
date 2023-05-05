package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object StringEntityTransform : EntityTransform<String>(EntityType.STRING, String::class) {

    override fun serializeEntity(entity: String, bytes: MutableList<Byte>) {
        bytes.add(EntityType.STRING.code)
        writeInt(entity.length, bytes)
        entity.encodeToByteArray().iterator().forEach { bytes.add(it) }
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): String {
        return readStr(bytes)
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is String
    }

}