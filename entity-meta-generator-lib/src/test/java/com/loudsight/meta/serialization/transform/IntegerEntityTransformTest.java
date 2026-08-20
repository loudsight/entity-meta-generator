package com.loudsight.meta.serialization.transform;

import java.util.ArrayList;

// All test cases live in the abstract NumberEntityTransformTest base class; PMD can't see
// inherited @Test methods, so it misreads this concrete fixture as a test class with no test cases.
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class IntegerEntityTransformTest extends NumberEntityTransformTest<Integer> {

    IntegerEntityTransformTest() {
        super(-1, 0, 10, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }


    @Override
    protected Iterable<Byte> serializeEntity(Integer entity) {
        var bytes = new ArrayList<Byte>();
        IntegerEntityTransform.getInstance().serializeEntity(entity, bytes);
        return bytes;
    }

    @Override
    protected Integer deserializeEntity(Iterable<Byte> bytes) {
        return IntegerEntityTransform.getInstance().deserializeEntity(bytes.iterator());
    }
}
