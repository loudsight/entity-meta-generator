package com.loudsight.meta.entity;

import java.util.List;
import java.util.function.Function;

public class EntityConstructor {

    private final Function<Object[], Object> constructor;
    private final List<EntityParameter> entityParameters;

    public EntityConstructor(List<EntityParameter> entityParameters, Function<Object[], Object> constructor) {
        this.constructor = constructor;
        this.entityParameters = entityParameters;
    }
    public <T> T newInstance(Object... parameters) {
        return (T)constructor.apply(parameters);
    }

    public List<EntityParameter> getEntityParameters() {
        return entityParameters;
    }
}