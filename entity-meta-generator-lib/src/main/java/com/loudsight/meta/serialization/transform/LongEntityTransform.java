package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class LongEntityTransform extends EntityTransform<Long> {

    private static class LongEntityTransformHolder {
        private static final LongEntityTransform INSTANCE = new LongEntityTransform();
    }
    // global access point
    public static LongEntityTransform getInstance() {
        return LongEntityTransform.LongEntityTransformHolder.INSTANCE;
    }
    private LongEntityTransform() {
        super(EntityType.LONG, Long.class);
    }


    @Override public void serializeEntity( Long entity, List<Byte> bytes) {
        bytes.add(EntityType.LONG.getCode());
        writeLong(entity, bytes);
    }

    @Override public Long  deserializeEntity(Iterator<Byte> bytes) {
        return readLong(bytes);
    }
}
