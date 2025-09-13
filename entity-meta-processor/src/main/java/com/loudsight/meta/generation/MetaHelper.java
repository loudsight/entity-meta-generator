package com.loudsight.meta.generation;

import com.loudsight.meta.Meta;

import java.util.Optional;

@FunctionalInterface
public interface MetaHelper {
    <T> Optional<Meta<T>> getMeta(Object classOrName);
}
