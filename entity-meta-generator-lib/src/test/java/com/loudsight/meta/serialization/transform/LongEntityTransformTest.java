package com.loudsight.meta.serialization.transform;


import java.util.ArrayList;


// All test cases live in the abstract NumberEntityTransformTest base class; PMD can't see
// inherited @Test methods, so it misreads this concrete fixture as a test class with no test cases.
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class LongEntityTransformTest extends NumberEntityTransformTest<Long> {

    LongEntityTransformTest() {
        super(new Long[]{-1L, 0L, 10L, Long.MAX_VALUE, Long.MIN_VALUE});
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
