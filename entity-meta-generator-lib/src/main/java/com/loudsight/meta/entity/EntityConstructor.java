package com.loudsight.meta.entity;

import java.util.List;
import java.util.function.Function;

/**
 * Class representing a constructor of an entity.
 */
public class EntityConstructor {

    /**
     * The constructor function.
     */
    private final Function<Object[], Object> constructor;
    /**
     * The constructor parameters.
     */
    private final List<EntityParameter> entityParameters;

    /**
     * Constructs an EntityConstructor.
     * @param entityParameters the constructor parameters
     * @param constructor the constructor function
     */
    public EntityConstructor(List<EntityParameter> entityParameters, Function<Object[], Object> constructor) {
        this.constructor = constructor;
        this.entityParameters = entityParameters;
    }
    /**
     * Creates a new instance using this constructor.
     * @param parameters the constructor parameters
     * @return the new instance
     */
    public <T> T newInstance(Object... parameters) {
        return (T)constructor.apply(parameters);
    }

    /**
     * Gets the constructor parameters.
     * @return list of parameters
     */
    public List<EntityParameter> getEntityParameters() {
        return entityParameters;
    }
}