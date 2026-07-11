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
public class SchemaFieldEntityTransform extends EntityTransform<SchemaField> {

    private static class SchemaFieldEntityTransformHolder {
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
        EntityTransform.serialize(entity.name(), bytes);
        EntityTransform.serialize(entity.typeName(), bytes);
        EntityTransform.serialize(entity.isEnum(), bytes);
        EntityTransform.serialize(entity.isCollection(), bytes);
        EntityTransform.serialize(entity.isId(), bytes);
        EntityTransform.serialize(entity.isTransient(), bytes);
    }

    @Override
    public SchemaField deserializeEntity(Iterator<Byte> bytes) {
        String name = EntityTransform.deserialize(bytes);
        String typeName = EntityTransform.deserialize(bytes);
        Boolean isEnum = EntityTransform.deserialize(bytes);
        Boolean isCollection = EntityTransform.deserialize(bytes);
        Boolean isId = EntityTransform.deserialize(bytes);
        Boolean isTransient = EntityTransform.deserialize(bytes);
        return new SchemaField(name, typeName, isEnum, isCollection, isId, isTransient);
    }
}
