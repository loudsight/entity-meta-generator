package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.List;

public class TemporalEntityTransform extends EntityTransform<Temporal> {

    private static class TemporalEntityTransformHolder {
        private static final TemporalEntityTransform INSTANCE = new TemporalEntityTransform();
    }
    // global access point
    public static TemporalEntityTransform getInstance() {
        return TemporalEntityTransform.TemporalEntityTransformHolder.INSTANCE;
    }
    private TemporalEntityTransform() {
        super(EntityType.DATETIME, Temporal.class);
    }


    @Override
    public void serializeEntity(Temporal entity, List<Byte> bytes) {
        bytes.add(EntityType.DATETIME.getCode());
        ZonedDateTime dt;
            // A ZonedDateTime carries a real offset, so it must be *converted* to UTC
            // (withZoneSameInstant), not relabelled (withZoneSameLocal) - the latter keeps the
            // wall-clock reading and silently moves the instant by that offset.
            // A LocalDateTime carries no offset and is already a UTC instant by contract (see
            // TimeProvider), so stamping the zone on is correct there.
            if (entity instanceof ZonedDateTime zdt)
                dt = zdt.withZoneSameInstant(ZoneOffset.UTC);
            else{
                dt = ((LocalDateTime)entity).atZone(
                        ZoneOffset.UTC
                );
            }
        var millis = dt.toInstant().toEpochMilli();
        var nanos = dt.getNano();
        writeLong(millis, bytes);
        writeInt(nanos, bytes);
    }

    @Override public Temporal  deserializeEntity(Iterator<Byte> bytes) {
        var millis = readLong(bytes);
        var nanos = readInt(bytes);

        return LocalDateTime.ofEpochSecond(Double.valueOf(millis / 1000.).longValue(), nanos, ZoneOffset.UTC);
    }
}
