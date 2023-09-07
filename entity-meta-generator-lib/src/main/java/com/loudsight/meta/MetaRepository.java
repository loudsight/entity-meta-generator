package com.loudsight.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MetaRepository {
        private static class MetaRepositoryHolder {
            private static final MetaRepository INSTANCE = new MetaRepository();
        }
        // global access point
        public static MetaRepository getInstance() {
            return MetaRepositoryHolder.INSTANCE;
        }
    private MetaRepository() {}

    private final Map<String, Meta<?>> metaByTypeName = new HashMap<>();
    private final Map<Class<?>, Meta<?>> metaByClass = new HashMap<>();

    public void register(Meta<?> meta) {
        metaByTypeName.put(meta.getTypeName(), meta);
        metaByClass.put(meta.getTypeClass(), meta);
    }

    public  <T> Meta<T> getMeta(Class<T> aClass) {
        return (Meta<T>)metaByClass.get(aClass);
    }

    public <T> Meta<T> getMeta(String typeName) {
        return (Meta<T>)metaByTypeName.get(typeName);
    }

    public Map<String, ?> toMap(Object entity) {
//        val meta = getMeta(entity::class)
//        if (meta == null) {
//            throw IllegalArgumentException("No meta found for entity: $entity")
//        }
        var entityMap = new HashMap<String, Object>();
//        entityMap.put("__typeName__", meta.typeName)
//
        if (entity instanceof Collection<?> entities) {
            entityMap.put("values", entities.stream().filter(Objects::nonNull).map(this::toMap).toList());
        } else {
//            meta.fields.forEach {
//                val value = it.getValue(entity)
//                if (value != null) {
//                    entityMap.put(it.name, value)
//                }
            }
        return entityMap;
    }
}