package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Introspect;

import java.util.Objects;

@Introspect(clazz = GenericType.class)
public class GenericType<T> {

    private Class<Integer> klazz;

    public Class<Integer> getKlazz() {
        return klazz;
    }

    public void setKlazz(Class<Integer> klazz) {
        this.klazz = klazz;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(klazz);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericType<?> genericType) {
            return Objects.equals(genericType.klazz, klazz);
        }
        return false;
    }
}
