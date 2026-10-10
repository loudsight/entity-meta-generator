package com.loudsight.meta.fixtures.processor;

import com.loudsight.meta.annotation.Introspect;

@Introspect(clazz = SimpleRecord.class)
public record SimpleRecord(int i, boolean b) {

    SimpleRecord(int i) {
        this(i, false);
    }

    SimpleRecord(boolean b) {
        this(0, b);
    }

}
