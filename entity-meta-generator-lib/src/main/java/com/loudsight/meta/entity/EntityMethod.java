package com.loudsight.meta.entity;

import java.util.List;


public record EntityMethod<T, R>(String name,
                                 List<EntityParameter> parameters,
                                 Class<R>     returnType,
                                 List<EntityAnnotation> annotations,
                                 Invoker<T, R> invoker) {

    interface Invoker<T, R> {
        R apply(T instance, Object... params);
    }

    R invoke(T instance, Object... params) {
        return invoker.apply(instance, params);
    }
}