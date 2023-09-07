package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class ByteEntityTransform extends EntityTransform<Byte> {

    private static class ByteEntityTransformHolder {
        private static final ByteEntityTransform INSTANCE = new ByteEntityTransform();
    }
    // global access point
    public static ByteEntityTransform getInstance() {
        return ByteEntityTransform.ByteEntityTransformHolder.INSTANCE;
    }
    private ByteEntityTransform() {
        super(EntityType.BYTE, Byte.class);
    }


    @Override public void serializeEntity( Byte entity, List<Byte> bytes) {
        bytes.add(EntityType.BYTE.getCode());
        bytes.add(entity);
    }

    @Override public Byte  deserializeEntity(Iterator<Byte> bytes) {
        return bytes.next();
    }
}