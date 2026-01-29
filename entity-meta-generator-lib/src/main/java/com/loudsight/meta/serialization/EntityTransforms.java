package com.loudsight.meta.serialization;

import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.serialization.transform.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityTransforms {
        private static class EntityTransformsHolder {
            private static final EntityTransforms INSTANCE = new EntityTransforms();
        }
        public static EntityTransforms getInstance() {
            return EntityTransformsHolder.INSTANCE;
        }
        private EntityTransforms() {}

        private final Map<Class<?>, EntityTransform<?>> classToEntityType = new LinkedHashMap<>();
    private final Map<EntityType, EntityTransform<?>> entityTypeToEntityTransform = new LinkedHashMap<>();

    {
        register(ClassTypeTransform.getInstance());
        register(BooleanEntityTransform.getInstance());
        register(ByteEntityTransform.getInstance());
        register(StringEntityTransform.getInstance());
        register(EnumEntityTransform.getInstance());
        register(LongEntityTransform.getInstance());
        register(DoubleEntityTransform.getInstance());
        register(IntegerEntityTransform.getInstance());
        register(ListEntityTransform.getInstance());
        register(SetEntityTransform.getInstance());
        register(MapEntityTransform.getInstance());
        register(LocalDateTimeEntityTransform.getInstance());
        register(CustomEntityTransform.getInstance());
    }

    public void register(EntityTransform<?> entityTransform) {
        entityTransform.getTargetClass().forEach(targetClass -> classToEntityType.put(targetClass, entityTransform));
        entityTypeToEntityTransform.put(entityTransform.entityType, entityTransform);
    }
    public <T> EntityTransform<T> getEntityTransform(EntityType entityType) {
        return (EntityTransform<T>)entityTypeToEntityTransform.get(entityType);
    }

    public <T> EntityTransform<T> getEntityTransform(Object entity) {
        if (entity == null) {
            return null;
        }

        for (var entry : classToEntityType.entrySet()) {
            var value = entry.getValue();
//            var key = entry.getKey();
            if (value instanceof CustomEntityTransform) {
                continue;
            }
            if (value.canTransform(entity)) {
                return (EntityTransform<T>)value;
            }
        }

        if (MetaRepository.getInstance().getMeta(entity.getClass()) != null) {
            return (EntityTransform<T>)classToEntityType.get(Object.class);
        }

        return null;
    }

    public EntityType getEntityType(Class<?> aClass, Object entity) {
        if (aClass.isArray()) {
            return EntityType.ARRAY;
        }
        if (isSubclassOf(aClass, Map.class)) {
            if (((Map<?, ?>)entity).containsKey("__className__")) {
                return EntityType.CUSTOM;
            }
        }
        var x = getEntityTransform(entity);
        if (x == null || x.entityType == EntityType.CUSTOM) {
            if (entity instanceof Enum<?>) {
                return EntityType.ENUM;
            }
            if (entity != null) {
                x = getEntityTransform(entity);
            }
            if (x == null) {
                return EntityType.CUSTOM;
            }
        }
        return x.entityType;
    }

    private static boolean isSubclassOf(Class<?> aClass, Class<?> bClass) {
        return aClass == bClass;
    }
}
