package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityType

object ListEntityTransform : CollectionEntityTransform<List<*>>(EntityType.LIST, List::class) {

    override fun newCollection(size: Int, init: (index: Int) -> Any?): List<*> {
        return MutableList(size, init)
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is List<*>
    }
}