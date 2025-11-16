package com.loudsight.meta.serialization.transform;

import java.util.Iterator;
import java.util.List;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

/**
 * EntityTransform implementation for Byte types.
 */
public class ByteEntityTransform extends EntityTransform<Byte> {

    /**
     * Holder for singleton instance.
     */
    private static class ByteEntityTransformHolder {
        private static final ByteEntityTransform INSTANCE = new ByteEntityTransform();
    }
    /**
     * Returns singleton instance of ByteEntityTransform.
     * @return the singleton instance
     */
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