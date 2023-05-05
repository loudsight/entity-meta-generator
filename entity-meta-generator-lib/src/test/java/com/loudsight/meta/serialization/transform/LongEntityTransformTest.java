package com.loudsight.meta.serialization.transform;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongEntityTransformTest {

    @Test
    public void testOne() {
        testValue(1L);
    }

    @Test
    public void testZero() {
        testValue(0L);
    }

    @Test
    public void testLong10() {
        testValue(10L);
    }

    @Test
    public void testLongMax() {
        testValue(Long.MAX_VALUE);
    }

    @Test
    public void testLongMin() {
        testValue(Long.MIN_VALUE);
    }

    private void testValue(long expected) {
        List<Byte> bytes = new ArrayList<>();
        LongEntityTransform.INSTANCE.serializeEntity(expected, bytes);
        bytes.remove(0);
        long actual = LongEntityTransform.INSTANCE.deserializeEntity(bytes.iterator());

        assertEquals(expected, actual);
    }
}
