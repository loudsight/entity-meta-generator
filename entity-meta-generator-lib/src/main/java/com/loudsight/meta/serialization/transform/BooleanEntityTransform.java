package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class BooleanEntityTransform extends EntityTransform<Boolean> {

private static class BooleanEntityTransformHolder {
    private static final BooleanEntityTransform INSTANCE = new BooleanEntityTransform();
}
    // global access point
    public static BooleanEntityTransform getInstance() {
        return BooleanEntityTransformHolder.INSTANCE;
    }
    private BooleanEntityTransform() {
        super(EntityType.BOOLEAN, boolean.class, Boolean.class);
    }

    @Override
    public void serializeEntity(Boolean entity, List<Byte> bytes) {
        bytes.add(EntityType.BOOLEAN.getCode());
        if (entity) {
            bytes.add(Integer.valueOf(1).byteValue());
        } else {
            bytes.add(Integer.valueOf(0).byteValue());
        }
    }

    @Override
    public Boolean deserializeEntity(Iterator<Byte> bytes) {
        return 1 - bytes.next() == 0;
    }
}