package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class DoubleEntityTransform extends EntityTransform<Double> {

    private static class DoubleEntityTransformHolder {
        private static final DoubleEntityTransform INSTANCE = new DoubleEntityTransform();
    }
    // global access point
    public static DoubleEntityTransform getInstance() {
        return DoubleEntityTransform.DoubleEntityTransformHolder.INSTANCE;
    }
    private DoubleEntityTransform() {
        super(EntityType.DOUBLE, Double.class);
    }


    @Override public void serializeEntity( Double entity, List<Byte> bytes) {
        bytes.add(EntityType.DOUBLE.getCode());
        writeLong(Double.doubleToRawLongBits(entity), bytes);
    }

    @Override public Double  deserializeEntity(Iterator<Byte> bytes) {
        var doubleBits = readLong(bytes);

        return Double.longBitsToDouble(doubleBits);
    }
}