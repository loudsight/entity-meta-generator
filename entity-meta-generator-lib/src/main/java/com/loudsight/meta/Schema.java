package com.loudsight.meta;

import com.loudsight.meta.entity.SchemaField;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class-free type metadata for Schema.
 * Contains only String/boolean/List data, no Class<?> references.
 * Used by persistence-server to understand type shapes without loading business entity classes.
 */
public record Schema(
    String typeName,
    String packageName,
    String simpleTypeName,
    boolean isEnum,
    boolean isRecord,
    List<SchemaField> fields,
    List<String> typeHierarchy
) {
    /**
     * Gets fields as a map keyed by field name.
     * @return map of fields by name
     */
    public Map<String, SchemaField> getFieldAsMap() {
        return fields.stream()
            .collect(Collectors.toMap(SchemaField::name, f -> f));
    }

    /**
     * Gets a field by name.
     * @param name the field name
     * @return the field, or null if not found
     */
    public SchemaField getFieldByName(String name) {
        return getFieldAsMap().get(name);
    }

    /**
     * Gets all ID fields (fields where isId is true).
     * @return list of ID fields
     */
    public List<SchemaField> getIdFields() {
        return fields.stream()
            .filter(SchemaField::isId)
            .collect(Collectors.toList());
    }

    /**
     * Content-derived identity of this schema's stored shape: a stable hash over the field set
     * (name, type, and the isId / isTransient / isEnum / isCollection flags), the type hierarchy
     * and record-ness. Field and hierarchy order does not affect it.
     * <p>
     * Two {@code Schema} instances built by different JVMs for the same {@code @Introspect} type
     * produce the same fingerprint; any change that alters how nodes of this type are written -
     * a field added, removed or retyped, an id or transient flag flipped, a supertype change -
     * produces a different one. persistence-server compares the fingerprint a connecting client
     * registers against the one the stored data was written under, to detect (and reject, not
     * migrate) a schema/data mismatch. See
     * {@code notebook/issues/persistence-schema-registry-lost-on-restart.md}.
     * @return lowercase-hex SHA-256 of this schema's canonical shape
     */
    public String fingerprint() {
        var canonical = new StringBuilder()
            .append(typeName).append('\n')
            .append("record=").append(isRecord).append('\n')
            .append("enum=").append(isEnum).append('\n');
        fields.stream()
            .sorted(Comparator.comparing(SchemaField::name))
            .forEach(f -> canonical
                .append("field:").append(f.name())
                .append('|').append(f.typeName())
                .append("|id=").append(f.isId())
                .append("|transient=").append(f.isTransient())
                .append("|enum=").append(f.isEnum())
                .append("|collection=").append(f.isCollection())
                .append('\n'));
        typeHierarchy.stream().sorted()
            .forEach(t -> canonical.append("super:").append(t).append('\n'));

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(canonical.toString().getBytes(StandardCharsets.UTF_8));
            var hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available in this JVM", e);
        }
    }
}
