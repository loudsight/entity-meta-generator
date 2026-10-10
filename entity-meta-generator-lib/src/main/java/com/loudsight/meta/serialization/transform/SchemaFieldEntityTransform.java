package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.entity.SchemaField;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

/**
 * Hand-written EntityTransform for SchemaField.
 * SchemaField cannot carry a generated Meta (entity-meta-generator-lib cannot depend on its own
 * annotation processor), so it is wire-serialized directly here, alongside the other hand-written
 * core-type transforms (e.g. TemporalEntityTransform).
 */
public final class SchemaFieldEntityTransform extends EntityTransform<SchemaField> {

    private static final class SchemaFieldEntityTransformHolder {
        private static final SchemaFieldEntityTransform INSTANCE = new SchemaFieldEntityTransform();
    }

    public static SchemaFieldEntityTransform getInstance() {
        return SchemaFieldEntityTransformHolder.INSTANCE;
    }

    private SchemaFieldEntityTransform() {
        super(EntityType.SCHEMA_FIELD, SchemaField.class);
    }

    @Override
    public void serializeEntity(SchemaField entity, List<Byte> bytes) {
        bytes.add(EntityType.SCHEMA_FIELD.getCode());
        serialize(entity.name(), bytes);
        serialize(entity.typeName(), bytes);
        serialize(entity.isEnum(), bytes);
        serialize(entity.isCollection(), bytes);
        serialize(entity.isId(), bytes);
        serialize(entity.isTransient(), bytes);
    }

    @Override
    public SchemaField deserializeEntity(Iterator<Byte> bytes) {
        String name = deserialize(bytes);
        String typeName = deserialize(bytes);
        Boolean isEnum = deserialize(bytes);
        Boolean isCollection = deserialize(bytes);
        Boolean isId = deserialize(bytes);
        Boolean isTransient = deserialize(bytes);
        return new SchemaField(name, typeName, isEnum, isCollection, isId, isTransient);
    }
}
