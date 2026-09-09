package com.loudsight.meta.entity;

import com.loudsight.useful.helper.ClassHelper;

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
        this.entityParameters = List.copyOf(entityParameters);
    }
    /**
     * Creates a new instance using this constructor.
     * @param parameters the constructor parameters
     * @return the new instance
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    public <T> T newInstance(Object... parameters) {
        return ClassHelper.uncheckedCast(constructor.apply(parameters));
    }

    /**
     * Gets the constructor parameters.
     * @return list of parameters
     */
    public List<EntityParameter> getEntityParameters() {
        return entityParameters;
    }
}