package com.loudsight.meta.serialization.transform;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class NumberEntityTransformTest<T extends Number> {

    private final T[] expectations;
    protected NumberEntityTransformTest(T...expectations){
        this.expectations = expectations;
    }



    protected abstract Iterable<Byte> serializeEntity(T entity);

    protected abstract T deserializeEntity(Iterable<Byte> bytes);

    @Test
    public void testExpectations() {
        for (T expected : expectations) {
            testValue(expected);
        }
    }

    protected void testValue(T expected) {
        Iterable<Byte> bytes = serializeEntity(expected);
        T actual = deserializeEntity(new Iterable<>() {
            private final Iterator<Byte> it = bytes.iterator();

            {
                it.next();
                it.remove();
            }

            @Override
            public Iterator<Byte> iterator() {
                return it;
            }
        });

        assertEquals(expected, actual);
    }
}
