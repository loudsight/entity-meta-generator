package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EnumEntityTransform extends EntityTransform<Enum<?>> {

    private static class EnumEntityTransformHolder {
        private static final EnumEntityTransform INSTANCE = new EnumEntityTransform();
    }
    // global access point
    public static EnumEntityTransform getInstance() {
        return EnumEntityTransform.EnumEntityTransformHolder.INSTANCE;
    }
    private EnumEntityTransform() {
        super(EntityType.ENUM, (Class<Enum<?>>)(Object)Enum.class);
    }


    @Override public void serializeEntity( Enum<?> entity, List<Byte> bytes) {
        bytes.add(EntityType.ENUM.getCode());

        var meta = MetaRepository.getInstance().getMeta(entity.getClass());

        writeStr(meta.getTypeName(), bytes);
        writeStr(entity.name(), bytes);
    }

    @Override public Enum<?>  deserializeEntity(Iterator<Byte> bytes) {
        var typeName = readStr(bytes);
        var meta = MetaRepository.getInstance().<Enum<?>>getMeta(typeName);
        var enumName = readStr(bytes);

        return meta.newInstance(Map.of("name", enumName));
    }
}
