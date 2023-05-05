package com.loudsight.meta.serialization.transform

import com.loudsight.meta.Meta
import com.loudsight.meta.MetaRepository
import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityTransforms
import com.loudsight.meta.serialization.EntityType

object MapEntityTransform : EntityTransform<Map<*, *>>(EntityType.MAP, Map::class) {

    override fun serializeEntity(entity: Map<*, *>, bytes: MutableList<Byte>) {
        bytes.add(EntityType.MAP.code)

        val fieldBytes = mutableListOf<Byte>()
        val fieldCount = entity
            .filter { it.value != null }
            .map {
                serialize(it.key, fieldBytes)
                serialize(it.value, fieldBytes)
            }.count()

        writeInt(fieldCount, bytes)
        bytes.addAll(fieldBytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Map<*, *> {
        val fieldCount = readInt(bytes)
        val fieldMap = mutableMapOf<Any, Any>()

        for (i in 0 until fieldCount) {
            val key = deserialize<Any>(bytes)
            val value = deserialize<Any>(bytes)
            fieldMap[key] = value
        }

        return fieldMap
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is Map<*, *>
    }

}