package com.loudsight.meta;

import com.loudsight.meta.entity.SchemaField;

import java.util.Collection;
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
}
