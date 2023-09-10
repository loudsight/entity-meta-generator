package com.loudsight.meta.serialization.transform;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongEntityTransformTest extends NumberEntityTransformTest<Long> {

    LongEntityTransformTest() {
        super(-1L, 0L, 10L, Long.MAX_VALUE, Long.MIN_VALUE);
    }


    @Override
    protected Iterable<Byte> serializeEntity(Long entity) {
        ArrayList<Byte> bytes = new ArrayList<>();
        LongEntityTransform.getInstance().serializeEntity(entity, bytes);

        return bytes;
    }

    @Override
    protected Long deserializeEntity(Iterable<Byte> bytes) {
        return LongEntityTransform.getInstance().deserializeEntity(bytes.iterator());
    }
}
