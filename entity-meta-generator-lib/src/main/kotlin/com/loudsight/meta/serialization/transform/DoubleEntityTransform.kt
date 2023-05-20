package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType

object DoubleEntityTransform : EntityTransform<Double>(EntityType.DOUBLE, Double::class) {

    override fun serializeEntity(entity: Double, bytes: MutableList<Byte>) {
        bytes.add(EntityType.DOUBLE.code)
        writeLong(entity.toBits(), bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Double {
        val doubleBits = readLong(bytes)

        return Double.fromBits(doubleBits)
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is Double
    }

}