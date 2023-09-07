package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class IntEntityTransform extends EntityTransform<Integer> {

    private static class IntEntityTransformHolder {
        private static final IntEntityTransform INSTANCE = new IntEntityTransform();
    }
    // global access point
    public static IntEntityTransform getInstance() {
        return IntEntityTransform.IntEntityTransformHolder.INSTANCE;
    }
    private IntEntityTransform() {
        super(EntityType.INTEGER, Integer.class);
    }


    @Override public void serializeEntity( Integer entity, List<Byte> bytes) {
        bytes.add(EntityType.INTEGER.getCode());
        writeInt(entity, bytes);
    }

    @Override public Integer  deserializeEntity(Iterator<Byte> bytes) {
        return readInt(bytes);
    }
}