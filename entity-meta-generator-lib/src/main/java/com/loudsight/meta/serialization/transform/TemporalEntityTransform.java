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
            if (entity instanceof ZonedDateTime zdt)
                dt = zdt.withZoneSameLocal(ZoneOffset.UTC);
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

        return LocalDateTime.ofEpochSecond(Double.valueOf((((double) millis))/1000.).longValue(), nanos, ZoneOffset.UTC);
    }
}
