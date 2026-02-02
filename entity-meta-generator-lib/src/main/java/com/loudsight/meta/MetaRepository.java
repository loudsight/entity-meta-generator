package com.loudsight.meta;

import com.loudsight.useful.helper.ClassHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MetaRepository {
    private static class MetaRepositoryHolder {
        private static final MetaRepository INSTANCE = new MetaRepository();
    }
    public static MetaRepository getInstance() {
        return MetaRepositoryHolder.INSTANCE;
    }

    private final Map<String, Meta<?>> metaByTypeName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Meta<?>> metaByClass = new ConcurrentHashMap<>();

    private static <T> Meta<T> loadMeta(Class<T> someClass) {
        return loadMeta(someClass.getName());
    }

    private static <T> Meta<T> loadMeta(String someClassName) {
        try {
            String metaClassName = someClassName + "Meta";
            
            // Load base class first to get its classloader
            Class<?> baseClass = Class.forName(someClassName);
            ClassLoader baseClassLoader = baseClass.getClassLoader();
            
            // Use base class classloader to load meta class
            Class<?> metaClass = Class.forName(metaClassName, true, baseClassLoader);
            
            // Use base class classloader for reflection by setting it as context
            ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(baseClassLoader);
                var method = metaClass.getMethod("getInstance");
                var result = method.invoke(null);
                return ClassHelper.uncheckedCast(result);
            } finally {
                Thread.currentThread().setContextClassLoader(originalContextClassLoader);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Meta<T> getMeta(Class<T> aClass) {
        try {
            return ClassHelper.uncheckedCast(metaByClass.computeIfAbsent(aClass, it -> {
                var meta = loadMeta(aClass);
                metaByTypeName.put(aClass.getName(), meta);
                return meta;
            }));
        } catch (Exception e) {
            return null;
        }
    }

    public <T> Meta<T> getMeta(String typeName) {
        try {
            return ClassHelper.uncheckedCast(metaByTypeName.computeIfAbsent(typeName, it -> {
                var meta = loadMeta(typeName);
                metaByClass.put(meta.getTypeClass(), meta);
                return meta;
            }));
        } catch (Exception e) {
            return null;
        }
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