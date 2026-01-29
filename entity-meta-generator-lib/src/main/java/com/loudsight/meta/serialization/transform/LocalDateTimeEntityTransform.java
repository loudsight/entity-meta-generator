package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

public class LocalDateTimeEntityTransform extends EntityTransform<LocalDateTime> {

    private static class LocalDateTimeEntityTransformHolder {
        private static final LocalDateTimeEntityTransform INSTANCE = new LocalDateTimeEntityTransform();
    }

    public static LocalDateTimeEntityTransform getInstance() {
        return LocalDateTimeEntityTransformHolder.INSTANCE;
    }

    private LocalDateTimeEntityTransform() {
        super(EntityType.DATETIME, LocalDateTime.class);
    }

    @Override
    public void serializeEntity(LocalDateTime entity, List<Byte> bytes) {
        bytes.add(EntityType.DATETIME.getCode());
        // Convert to epoch millis in UTC for serialization
        long epochMilli = entity.toInstant(ZoneOffset.UTC).toEpochMilli();
        writeLong(epochMilli, bytes);
    }

    @Override
    public LocalDateTime deserializeEntity(Iterator<Byte> bytes) {
        long epochMilli = readLong(bytes);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
    }
}
