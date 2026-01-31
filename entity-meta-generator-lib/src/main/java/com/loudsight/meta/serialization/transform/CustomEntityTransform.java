package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.Meta;
import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import com.loudsight.useful.helper.logging.LoggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CustomEntityTransform extends EntityTransform<Object> { 
    private static final LoggingHelper logger = LoggingHelper.wrap(CustomEntityTransform.class);
    
    private static final int MAX_SERIALIZATION_DEPTH = 100;
    
    private static final ThreadLocal<Set<Object>> SERIALIZATION_STACK = 
            ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));
    
    private static final ThreadLocal<Integer> SERIALIZATION_DEPTH = 
            ThreadLocal.withInitial(() -> 0);

    private static class CustomEntityTransformHolder {
        private static final CustomEntityTransform INSTANCE = new CustomEntityTransform();
    }
    // global access point
    public static CustomEntityTransform getInstance() {
        return CustomEntityTransformHolder.INSTANCE;
    }
    private CustomEntityTransform() {
        super(EntityType.CUSTOM, Object.class);
    }


    @Override public void serializeEntity( Object entity, List<Byte> bytes) {
        serializeEntityX(entity, bytes);
    }

    private <T> void serializeEntityX(T entity, List<Byte> bytes) {
        Set<Object> stack = SERIALIZATION_STACK.get();
        int depth = SERIALIZATION_DEPTH.get();
        
        try {
            if (stack.contains(entity)) {
                String errorMsg = String.format(
                        "Circular reference detected while serializing %s (identity=%d). " +
                        "Serialization stack contains %d objects. Entity: %s",
                        entity.getClass().getName(),
                        System.identityHashCode(entity),
                        stack.size(),
                        safeToString(entity));
                logger.logError(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            
            if (depth > MAX_SERIALIZATION_DEPTH) {
                String errorMsg = String.format(
                        "Max serialization depth (%d) exceeded while serializing %s. " +
                        "This may indicate a circular reference or deeply nested structure. Entity: %s",
                        MAX_SERIALIZATION_DEPTH,
                        entity.getClass().getName(),
                        safeToString(entity));
                logger.logError(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            
            stack.add(entity);
            SERIALIZATION_DEPTH.set(depth + 1);
            
            var meta = MetaRepository.getInstance().getMeta((Class<T>)entity.getClass());
            if (meta == null) {
                throw new IllegalArgumentException("Unknown entity class: " + entity.getClass().getSimpleName());
            }

                logger.logTrace("Serializing entity: type={}, depth={}, stackSize={}", 
                        meta.getTypeName(), depth, stack.size());

            bytes.add(EntityType.CUSTOM.getCode());
            writeStr(meta.getTypeName(), bytes);

            var fieldBytes = new ArrayList<Byte>();

            var fieldCount = meta.getFields()
                    .stream()
                .filter(it -> it.get(entity) != null)
                .map(it -> {
                    writeStr(it.name(), fieldBytes);
                    var fieldValue = it.get(entity);
                        logger.logTrace("  Serializing field: name={}, valueType={}", 
                                it.name(), 
                                fieldValue != null ? fieldValue.getClass().getSimpleName() : "null");
                    serialize(fieldValue, fieldBytes);
                    return 0;
                }).count();
            
            writeInt(Long.valueOf(fieldCount).intValue(), bytes);
            bytes.addAll(fieldBytes);
        } finally {
            stack.remove(entity);
            SERIALIZATION_DEPTH.set(depth);
            if (depth == 0) {
                stack.clear();
            }
        }
    }
    
    private String safeToString(Object entity) {
        try {
            String str = entity.toString();
            if (str.length() > 200) {
                return str.substring(0, 200) + "...";
            }
            return str;
        } catch (Exception e) {
            return entity.getClass().getName() + "@" + System.identityHashCode(entity) + 
                    " (toString failed: " + e.getMessage() + ")";
        }
    }

    @Override public Object  deserializeEntity(Iterator<Byte> bytes) {
        var typeName = readStr(bytes);
        Meta<Object> meta = MetaRepository.getInstance().getMeta(typeName);
        if (meta == null) {
            throw new IllegalArgumentException("Unknown entity type-name: " + typeName);
        }
        var fieldCount = readInt(bytes);
        var fieldMap = new HashMap<String, Object>();

        for (int i  = 0; i  < fieldCount; i ++) {
            var fieldName = readStr(bytes);
            var fieldValue = deserialize(bytes);
            fieldMap.put(fieldName, fieldValue);
        }

        return meta.newInstance(fieldMap);
    }


    @Override public  boolean canTransform(Object entity) {
        throw new IllegalStateException("Unexpected call to CustomEntityTransform.canTransform");
    }

}
