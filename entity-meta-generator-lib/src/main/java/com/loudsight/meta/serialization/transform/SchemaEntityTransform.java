package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.Schema;
import com.loudsight.meta.entity.SchemaField;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

/**
 * Hand-written EntityTransform for Schema.
 * Schema cannot carry a generated Meta (entity-meta-generator-lib cannot depend on its own
 * annotation processor), so it is wire-serialized directly here, alongside the other hand-written
 * core-type transforms (e.g. TemporalEntityTransform). This is required for schema registration
 * (PersistenceApiClient sends List&lt;Schema&gt; over Aeron) to work at all.
 */
public final class SchemaEntityTransform extends EntityTransform<Schema> {

    private static final class SchemaEntityTransformHolder {
        private static final SchemaEntityTransform INSTANCE = new SchemaEntityTransform();
    }

    public static SchemaEntityTransform getInstance() {
        return SchemaEntityTransformHolder.INSTANCE;
    }

    private SchemaEntityTransform() {
        super(EntityType.SCHEMA, Schema.class);
    }

    @Override
    public void serializeEntity(Schema entity, List<Byte> bytes) {
        bytes.add(EntityType.SCHEMA.getCode());
        serialize(entity.typeName(), bytes);
        serialize(entity.packageName(), bytes);
        serialize(entity.simpleTypeName(), bytes);
        serialize(entity.isEnum(), bytes);
        serialize(entity.isRecord(), bytes);
        serialize(entity.fields(), bytes);
        serialize(entity.typeHierarchy(), bytes);
    }

    @Override
    public Schema deserializeEntity(Iterator<Byte> bytes) {
        String typeName = deserialize(bytes);
        String packageName = deserialize(bytes);
        String simpleTypeName = deserialize(bytes);
        Boolean isEnum = deserialize(bytes);
        Boolean isRecord = deserialize(bytes);
        List<SchemaField> fields = deserialize(bytes);
        List<String> typeHierarchy = deserialize(bytes);
        return new Schema(typeName, packageName, simpleTypeName, isEnum, isRecord, fields, typeHierarchy);
    }
}
