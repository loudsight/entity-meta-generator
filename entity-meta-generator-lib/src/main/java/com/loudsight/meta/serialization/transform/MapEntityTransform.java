package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapEntityTransform extends EntityTransform<Map<?, ?>> {

    private static class MapEntityTransformHolder {
        private static final MapEntityTransform INSTANCE = new MapEntityTransform();
    }
    // global access point
    public static MapEntityTransform getInstance() {
        return MapEntityTransform.MapEntityTransformHolder.INSTANCE;
    }
    private MapEntityTransform() {
        super(EntityType.MAP, (Class<Map<?,?>>)(Object)Map.class);
    }


    @Override public void serializeEntity( Map<?, ?> entity, List<Byte> bytes) {
        bytes.add(EntityType.MAP.getCode());

        var fieldBytes = new ArrayList<Byte>();
        var fieldCount = entity.entrySet().stream()
            .filter(it -> it.getValue() != null)
            .map(it -> {
                serialize(it.getKey(), fieldBytes);
                serialize(it.getValue(), fieldBytes);
                return 0;
            }).count();

        writeInt(Long.valueOf(fieldCount).intValue(), bytes);
        bytes.addAll(fieldBytes);
    }

    @Override public Map<?, ?>  deserializeEntity(Iterator<Byte> bytes) {
        var fieldCount = readInt(bytes);
        var fieldMap = new HashMap<>();

        for (int i  = 0; i  < fieldCount; i ++) {
            var key = EntityTransform.<String>deserialize(bytes);
            var value = deserialize(bytes);
            fieldMap.put(key, value);
        }

        return fieldMap;
    }
}