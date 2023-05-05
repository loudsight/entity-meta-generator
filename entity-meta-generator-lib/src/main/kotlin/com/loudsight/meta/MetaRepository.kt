package com.loudsight.meta

object MetaRepository {

    private val metaByTypeName = mutableMapOf<String, Meta<*>>()
    private val metaByClass = mutableMapOf<Class<*>, Meta<*>>()

    fun register(meta: Meta<*>) {
        metaByTypeName[meta.typeName] = meta
        metaByClass[meta.typeClass] = meta
    }

    fun <T : Any> getMeta(aClass: Class<T>): Meta<T>? {
        return metaByClass[aClass] as Meta<T>?
    }

    fun <T : Any> getMeta(typeName: String): Meta<T>? {
        return metaByTypeName[typeName] as Meta<T>?
    }

    fun toMap(entity: Any): Map<String, Any> {
//        val meta = getMeta(entity::class)
//        if (meta == null) {
//            throw IllegalArgumentException("No meta found for entity: $entity")
//        }
        val entityMap = mutableMapOf<String, Any>()
//        entityMap["__typeName__"] = meta.typeName
//
        if (entity is Collection<*>) {
            entityMap["values"] = entity.filterNotNull().map {
                toMap(it)
            }.toList()
        } else {
//            meta.fields.forEach {
//                val value = it.getValue(entity)
//                if (value != null) {
//                    entityMap[it.name] = value
//                }
            }
        return entityMap
    }
}