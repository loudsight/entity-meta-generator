package com.loudsight.meta.serialization.transform

import com.loudsight.meta.serialization.EntityTransform
import com.loudsight.meta.serialization.EntityType
import com.loudsight.useful.helper.ClassHelper
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal

object TemporalEntityTransform : EntityTransform<Temporal>(EntityType.DATETIME, Temporal::class) {

    override fun serializeEntity(entity: Temporal, bytes: MutableList<Byte>) {
        bytes.add(EntityType.DATETIME.code)
        val zdt: ZonedDateTime =
            if (entity is ZonedDateTime)
                entity.withZoneSameLocal(ZoneOffset.UTC)
            else (entity as LocalDateTime).atZone(
                ZoneOffset.UTC
            )
        val millis = zdt.toInstant().toEpochMilli()
        val nanos = zdt.nano
        writeLong(millis, bytes)
        writeInt(nanos, bytes)
    }

    override fun deserializeEntity(bytes: Iterator<Byte>): Temporal {
        val millis = readLong(bytes)
        val nanos = readInt(bytes)

        return LocalDateTime.ofEpochSecond(millis / 1000, nanos, ZoneOffset.UTC)
    }


    override fun canTransform(entity: Any): Boolean {
        return entity is Temporal
    }

}