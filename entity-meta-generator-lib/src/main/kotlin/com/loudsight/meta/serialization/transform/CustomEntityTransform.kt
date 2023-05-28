package com.loudsight.meta.serialization.transform

import com.loudsight.meta.Meta
import com.loudsight.meta.MetaRepository
import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object CustomEntityTransform : EntityTransform<Any>(EntityType.CUSTOM, Any::class) {

    override fun serializeEntity(entity: Any, bytes: MutableList<Byte>) {
        serializeEntityX(entity, bytes)
    }

    private fun <T: Any> serializeEntityX(entity: T, bytes: MutableList<Byte>) {

        val meta = MetaRepository.getMeta(entity::class.java as Class<T>)
            ?: throw IllegalArgumentException("Unknown entity class: " + entity::class.simpleName)

        bytes.add(EntityType.CUSTOM.code)
        writeStr(meta.typeName, bytes)

        val fieldBytes = mutableListOf<Byte>()

//        val subtypeFields = meta.typeHierarchy
//            .map { MetaRepository.getMeta(it) as Meta<T> }
//            .flatMap { it.fields }
//
//        val fields = Stream.concat(subtypeFields.stream(), meta.fields.stream())
//            .collect(Collectors.toList())


        val fieldCount = meta.fields
            .filter { it[entity] != null }
            .map {
                writeStr(it.name, fieldBytes)
                val fieldValue = it[entity]!!
                serialize(fieldValue, fieldBytes)
            }.count()
        
        writeInt(fieldCount, bytes)
        bytes.addAll(fieldBytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Any {
        val typeName = readStr(bytes)
        val meta: Meta<Any> = MetaRepository.getMeta(typeName)
            ?: throw IllegalArgumentException("Unknown entity type-name: $typeName")
        val fieldCount = readInt(bytes)
        val fieldMap = mutableMapOf<String, Any>()
//        fieldMap["__className__"] = typeName

        for (i in 0 until fieldCount) {
            val fieldName = readStr(bytes)
            val fieldValue = deserialize<Any>(bytes)
            fieldMap[fieldName] = fieldValue
        }

        return meta.newInstance(fieldMap)
    }


    override fun canTransform(entity: Any): Boolean {
        throw IllegalStateException("Unexpected call to CustomEntityTransform.canTransform")
    }

}
