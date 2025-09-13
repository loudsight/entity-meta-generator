package com.loudsight.meta.serialization;


@FunctionalInterface
public interface TypeConverter<T, F> {
    T convert(F from);
}