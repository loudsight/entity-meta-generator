package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityTransforms;
import com.loudsight.meta.serialization.EntityType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class CollectionEntityTransform<T extends Collection<?>> extends EntityTransform<T> {
    public CollectionEntityTransform(EntityType entityType, Class<T> kClass) {
        super(entityType, kClass);
    }

    abstract T newCollection(int size, Function<Integer, Object> init);

    @Override public void serializeEntity( T entity, List<Byte> bytes) {
        bytes.add(entityType.getCode());
        writeInt(entity.size(), bytes);

        entity.forEach(it -> {
            if (it == null) {
                bytes.add(EntityType.NULL.getCode());
            } else {
                EntityTransform<Object> entityTransform = EntityTransforms.getInstance().getEntityTransform(it);
                entityTransform.serializeEntity(it, bytes);
            }
        });
    }

    @Override public T  deserializeEntity(Iterator<Byte> bytes) {
        var size = readInt(bytes);

        return newCollection(size, it -> {
            var entityType1 = EntityType.getEntityType(bytes.next());
            var transform = EntityTransforms.getInstance().getEntityTransform(entityType1);
            return transform.deserializeEntity(bytes);
        });
    }
}