package com.loudsight.meta;

import com.loudsight.meta.entity.SchemaField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit test for {@link Schema#fingerprint()} - the content-derived shape identity that
 * persistence-server compares against a label's stored data to detect a schema mismatch.
 * See {@code notebook/issues/persistence-schema-registry-lost-on-restart.md}.
 */
class SchemaFingerprintTest {

    private static Schema schema(List<SchemaField> fields, List<String> hierarchy) {
        return new Schema("com.example.Thing", "com.example", "Thing", false, false, fields, hierarchy);
    }

    private static SchemaField field(String name, String type, boolean isId, boolean isTransient) {
        return new SchemaField(name, type, false, false, isId, isTransient);
    }

    @Test
    void isStableAcrossInstancesWithTheSameShape() {
        var a = schema(List.of(field("id", "long", true, false), field("name", "java.lang.String", false, false)), List.of());
        var b = schema(List.of(field("id", "long", true, false), field("name", "java.lang.String", false, false)), List.of());
        assertEquals(a.fingerprint(), b.fingerprint());
        assertEquals(64, a.fingerprint().length(), "SHA-256 hex is 64 chars");
    }

    @Test
    void isIndependentOfFieldOrder() {
        var ordered = schema(List.of(field("id", "long", true, false), field("name", "java.lang.String", false, false)), List.of());
        var shuffled = schema(List.of(field("name", "java.lang.String", false, false), field("id", "long", true, false)), List.of());
        assertEquals(ordered.fingerprint(), shuffled.fingerprint());
    }

    @Test
    void changesWhenAFieldIsAdded() {
        var before = schema(List.of(field("id", "long", true, false)), List.of());
        var after = schema(List.of(field("id", "long", true, false), field("added", "int", false, false)), List.of());
        assertNotEquals(before.fingerprint(), after.fingerprint());
    }

    @Test
    void changesWhenAFieldIsRetyped() {
        var asInt = schema(List.of(field("qty", "int", false, false)), List.of());
        var asLong = schema(List.of(field("qty", "long", false, false)), List.of());
        assertNotEquals(asInt.fingerprint(), asLong.fingerprint());
    }

    @Test
    void changesWhenTheIdFlagMoves() {
        var idOnA = schema(List.of(field("a", "long", true, false), field("b", "long", false, false)), List.of());
        var idOnB = schema(List.of(field("a", "long", false, false), field("b", "long", true, false)), List.of());
        assertNotEquals(idOnA.fingerprint(), idOnB.fingerprint());
    }

    @Test
    void changesWhenTheTypeHierarchyChanges() {
        var plain = schema(List.of(field("id", "long", true, false)), List.of());
        var subtyped = schema(List.of(field("id", "long", true, false)), List.of("com.example.Base"));
        assertNotEquals(plain.fingerprint(), subtyped.fingerprint());
    }
}
