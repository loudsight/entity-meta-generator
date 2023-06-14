package com.loudsight.meta.serialization

import com.loudsight.meta.serialization.transform.StringEntityTransform
import kotlin.reflect.KClass

abstract class EntityTransform<T : Any>(val entityType: EntityType, val kClass: KClass<T>) {

    abstract fun canTransform(entity: Any): Boolean

    abstract fun serializeEntity(entity: T, bytes: MutableList<Byte>)

    abstract fun deserializeEntity(bytes: Iterator<Byte>): T

    protected fun writeStr(str: String, bytes: MutableList<Byte>) {
        val length = str.length
        writeInt(length, bytes)
        str.forEach { e: Char -> bytes.add(e.code.toByte()) }
    }

    protected fun writeLong(value: Long, bytes: MutableList<Byte>) {
        var acc = value
        for (i in 0 until 8) {
            bytes.add((acc and 0xFF).toByte())
            acc = acc shr 8
        }
    }

    protected fun readLong(bytes: Iterator<Byte>): Long {
        var value = 0L
        for (i in 0 until 8) {
            value += (bytes.next().toLong() and 0xff) shl i * 8
        }

        return value
    }

    protected fun writeInt(length: Int, bytes: MutableList<Byte>) {
        for (i in 0 until 4) {
            bytes.add(((length shr i * 8) and 0xFF).toByte())
        }
    }

    protected fun readInt(bytes: Iterator<Byte>): Int {
        var length = 0
        for (i in 0 until 4) {
            length += bytes.next().toInt() and 0xff shl i * 8
        }
        return length
    }


    companion object {
        fun readStr(bytes: Iterator<Byte>): String {
            val length = StringEntityTransform.readInt(bytes)
            val outBytes = mutableListOf<Byte>()
            for (i in 0 until length) {
                outBytes.add(bytes.next())
            }

            return outBytes.toByteArray().decodeToString()
        }
        fun <T : Any> serialize(entity: T?): ByteArray {
            val outBytes = mutableListOf<Byte>()
            serialize(entity, outBytes)
            return outBytes.toByteArray()
        }

        fun <T : Any> serialize(entity: T?, bytes: MutableList<Byte>) {
            if (entity == null) {
                bytes.add(EntityType.NULL.code)
                return
            }
            val entityTransform: EntityTransform<T>? =  EntityTransforms.getEntityTransform(entity)

            if (entityTransform != null) {
                entityTransform.serializeEntity(entity, bytes)
            } else {
                throw IllegalStateException("No transformer found for ${entity::class}")
            }
        }

        fun <T> deserialize(bytes: ByteArray): T {
            return deserialize(bytes, 0, bytes.size)
        }

        fun <T> deserialize(bytes: ByteArray, offset: Int, length: Int): T {
            val byteIterator = object : ByteIterator() {
                var index = offset

                override fun hasNext(): Boolean {
                    return index < length
                }

                override fun nextByte(): Byte {
                    bytes[index].let {
                        index++
                        return it
                    }
                }
            }

            return deserialize(byteIterator)
        }

        fun <T> deserialize(bytes: Iterator<Byte>): T {
            val entityType = EntityType.getEntityType(bytes.next().toInt().toChar())
            val entityTransform = EntityTransforms.getEntityTransform<Any>(entityType)

            return entityTransform?.deserializeEntity(bytes) as T
        }
    }
}