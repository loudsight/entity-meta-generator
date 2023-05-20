package com.loudsight.meta.serialization

import com.loudsight.meta.MetaRepository
import com.loudsight.meta.serialization.transform.*
import kotlin.reflect.KClass

object EntityTransforms {
    private val classToEntityType: MutableMap<KClass<*>, EntityTransform<*>> = LinkedHashMap()
    private val entityTypeToEntityTransform: MutableMap<EntityType, EntityTransform<*>> = LinkedHashMap()

    init {
        register(BooleanEntityTransform)
        register(ByteEntityTransform)
        register(StringEntityTransform)
        register(EnumEntityTransform)
        register(LongEntityTransform)
        register(DoubleEntityTransform)
        register(IntEntityTransform)
        register(ListEntityTransform)
        register(SetEntityTransform)
        register(MapEntityTransform)
        register(CustomEntityTransform)
    }

    fun register(entityTransform: EntityTransform<*>) {
        classToEntityType[entityTransform.kClass] = entityTransform
        entityTypeToEntityTransform[entityTransform.entityType] = entityTransform
    }
    fun <T: Any> getEntityTransform(entityType: EntityType): EntityTransform<T>? {
        return entityTypeToEntityTransform[entityType] as EntityTransform<T>?
    }

    fun <T: Any> getEntityTransform(entity: Any?): EntityTransform<T>? {
        if (entity == null) {
            return null
        }

        for ((_, value) in classToEntityType) {
            if (value is CustomEntityTransform) {
                continue;
            }
            if (value.canTransform(entity)) {
                return value as EntityTransform<T>
            }
        }

        if (MetaRepository.getMeta(entity::class.java) != null) {
            return classToEntityType[Any::class] as EntityTransform<T>
        }

        return null
    }

    fun getEntityType(aClass: KClass<*>, entity: Any?): EntityType {
        if (aClass is Array<*>) {
            return EntityType.ARRAY
        }
        if (isSubclassOf(aClass, Map::class)) {
            if ((entity as Map<*, *>?)!!.containsKey("__className__")) {
                return EntityType.CUSTOM
            }
        }
        var x = getEntityTransform<Any>(entity)
        if (x == null || x.entityType === EntityType.CUSTOM) {
            if (entity is Enum<*>) {
                return EntityType.ENUM
            }
            if (entity != null) {
                x = getEntityTransform(entity)
            }
            if (x == null) {
                return EntityType.CUSTOM
            }
        }
        return x.entityType
    }

    fun isSubclassOf(aClass: KClass<*>, bClass: KClass<*>): Boolean {
        if (aClass == bClass) {
            return true
        }
        return false
    }
}