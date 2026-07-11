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
public class SchemaEntityTransform extends EntityTransform<Schema> {

    private static class SchemaEntityTransformHolder {
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
        EntityTransform.serialize(entity.typeName(), bytes);
        EntityTransform.serialize(entity.packageName(), bytes);
        EntityTransform.serialize(entity.simpleTypeName(), bytes);
        EntityTransform.serialize(entity.isEnum(), bytes);
        EntityTransform.serialize(entity.isRecord(), bytes);
        EntityTransform.serialize(entity.fields(), bytes);
        EntityTransform.serialize(entity.typeHierarchy(), bytes);
    }

    @Override
    public Schema deserializeEntity(Iterator<Byte> bytes) {
        String typeName = EntityTransform.deserialize(bytes);
        String packageName = EntityTransform.deserialize(bytes);
        String simpleTypeName = EntityTransform.deserialize(bytes);
        Boolean isEnum = EntityTransform.deserialize(bytes);
        Boolean isRecord = EntityTransform.deserialize(bytes);
        List<SchemaField> fields = EntityTransform.deserialize(bytes);
        List<String> typeHierarchy = EntityTransform.deserialize(bytes);
        return new Schema(typeName, packageName, simpleTypeName, isEnum, isRecord, fields, typeHierarchy);
    }
}
