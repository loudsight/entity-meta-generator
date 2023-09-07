package com.loudsight.meta.serialization;


public interface TypeConverter<T, F> {
    T convert(F from);
}