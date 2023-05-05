package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityType

object SetEntityTransform : CollectionEntityTransform<Set<*>>(EntityType.SET, Set::class) {

    override fun newCollection(size: Int, init: (index: Int) -> Any?): Set<*> {
        val set = HashSet<Any>(size)
        for (i in 0 until size) {
            set.add(init.invoke(i) as Any)
        }
        return set
    }

    override fun canTransform(entity: Any): Boolean {
        return entity is Set<*>
    }
}