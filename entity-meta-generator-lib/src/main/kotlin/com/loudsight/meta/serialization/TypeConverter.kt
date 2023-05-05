package com.loudsight.meta.serialization


interface TypeConverter<T, F> {
    fun convert(from: F?): T?
}