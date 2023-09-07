package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListEntityTransform extends CollectionEntityTransform<List<?>> {

private static class ListEntityTransformHolder {
    private static final ListEntityTransform INSTANCE = new ListEntityTransform();
}
    // global access point
    public static ListEntityTransform getInstance() {
        return ListEntityTransformHolder.INSTANCE;
    }


    private ListEntityTransform() {
        super(EntityType.SET, (Class<List<?>>)(Object)List.class);
    }

    @Override public List<?> newCollection(int size, Function<Integer, Object> init) {
        var list = new ArrayList<>(size);
        for (int i  = 0; i  < size; i ++) {
            list.add(init.apply(i));
        }
        return list;
    }
}
