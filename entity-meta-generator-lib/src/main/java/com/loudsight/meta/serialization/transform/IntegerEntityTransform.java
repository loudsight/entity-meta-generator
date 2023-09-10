package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class IntegerEntityTransform extends EntityTransform<Integer> {

    private static class IntEntityTransformHolder {
        private static final IntegerEntityTransform INSTANCE = new IntegerEntityTransform();
    }
    // global access point
    public static IntegerEntityTransform getInstance() {
        return IntegerEntityTransform.IntEntityTransformHolder.INSTANCE;
    }
    private IntegerEntityTransform() {
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