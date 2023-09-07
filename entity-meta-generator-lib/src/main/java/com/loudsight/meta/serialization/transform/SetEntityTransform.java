package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SetEntityTransform extends CollectionEntityTransform<Set<?>> {

    private static class SetEntityTransformHolder {
        private static final SetEntityTransform INSTANCE = new SetEntityTransform();
    }
    // global access point
    public static SetEntityTransform getInstance() {
        return SetEntityTransformHolder.INSTANCE;
    }


    private SetEntityTransform() {
        super(EntityType.SET, (Class<Set<?>>)(Object)Set.class);
    }

    @Override public Set<?> newCollection(int size, Function<Integer, Object> init) {
        var set = new HashSet<>(size);
        for (int i  = 0; i  < size; i ++) {
            set.add(init.apply(i));
        }
        return set;
    }
}