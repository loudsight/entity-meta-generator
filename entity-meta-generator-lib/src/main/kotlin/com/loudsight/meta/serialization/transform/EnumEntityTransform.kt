package com.loudsight.meta.serialization.transform

import com.loudsight.meta.MetaRepository
import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object EnumEntityTransform : EntityTransform<Enum<*>>(EntityType.ENUM, Enum::class) {

    override fun serializeEntity(entity: Enum<*>, bytes: MutableList<Byte>) {
        bytes.add(EntityType.ENUM.code)

        val meta = MetaRepository.getMeta(entity::class.java)

        writeStr(meta!!.typeName, bytes)
        writeStr(entity.name, bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Enum<*> {
        val typeName = readStr(bytes)
        val meta = MetaRepository.getMeta<Enum<*>>(typeName)
        val enumName = readStr(bytes)

        return meta!!.newInstance(
            mapOf(
                Pair("name", enumName),
            )
        )
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is Enum<*>
    }
}
