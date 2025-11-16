package com.loudsight.meta.entity;

import java.util.List;


/**
 * Record representing a method of an entity.
 * @param <T> the entity type
 * @param <R> the return type
 * @param name the method name
 * @param parameters the method parameters
 * @param returnType the return type class
 * @param annotations the annotations on this method
 * @param invoker the function to invoke the method
 */
public record EntityMethod<T, R>(String name,
                                 List<EntityParameter> parameters,
                                 Class<R>     returnType,
                                 List<EntityAnnotation> annotations,
                                 Invoker<T, R> invoker) {

    @FunctionalInterface
    interface Invoker<T, R> {
        /**
         * Invokes the method on the given instance.
         * @param instance the instance
         * @param params the parameters
         * @return the result
         */
        R apply(T instance, Object... params);
    }

    /**
     * Invokes this method on the given instance.
     * @param instance the instance
     * @param params the parameters
     * @return the result
     */
    public R invoke(T instance, Object... params) {
        return invoker.apply(instance, params);
    }
}