package com.loudsight.codemap.model;

/**
 * A public static final field. {@code value} is the compile-time constant's source-like
 * representation (e.g. a string constant is rendered quoted) when the compiler can resolve one,
 * else {@code null}.
 */
public record FieldSignature(String name, String type, String value) {
}
