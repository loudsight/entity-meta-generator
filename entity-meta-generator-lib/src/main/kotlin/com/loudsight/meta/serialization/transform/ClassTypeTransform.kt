package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType
import kotlin.reflect.KClass

object ClassTypeTransform : EntityTransform<Class<*>>(EntityType.CLASS, Class::class) {

    override fun serializeEntity(entity: Class<*>, bytes: MutableList<Byte>) {
        bytes.add(EntityType.CLASS.code)
        writeStr(entity.name, bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Class<*> {
        val className = readStr(bytes)

        return Class.forName(className)
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is Class<*>
    }
}