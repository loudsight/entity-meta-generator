package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EnumEntityTransform extends EntityTransform<Object> {

    private static class EnumEntityTransformHolder {
        private static final EnumEntityTransform INSTANCE = new EnumEntityTransform();
    }
    // global access point
    public static EnumEntityTransform getInstance() {
        return EnumEntityTransform.EnumEntityTransformHolder.INSTANCE;
    }
    private EnumEntityTransform() {
        super(EntityType.ENUM, Enum.class);
    }


    @Override public void serializeEntity( Object entity, List<Byte> bytes) {
        bytes.add(EntityType.ENUM.getCode());

        var meta = MetaRepository.getInstance().getMeta(entity.getClass());

        writeStr(meta.getTypeName(), bytes);
        writeStr(((Enum<?>)entity).name(), bytes);
    }

    @Override public Object  deserializeEntity(Iterator<Byte> bytes) {
        var typeName = readStr(bytes);
        var meta = MetaRepository.getInstance().<Enum<?>>getMeta(typeName);
        var enumName = readStr(bytes);

        if (meta == null) {
            // Graceful degrade: return bare enum-name String
            // This occurs on persistence-server for business types not on classpath
            return enumName;
        }

        return meta.newInstance(Map.of("name", enumName));
    }
}
