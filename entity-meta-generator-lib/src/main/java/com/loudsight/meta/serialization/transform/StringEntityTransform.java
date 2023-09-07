package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class StringEntityTransform extends EntityTransform<String> {

    private static class StringEntityTransformHolder {
        private static final StringEntityTransform INSTANCE = new StringEntityTransform();
    }
    // global access point
    public static StringEntityTransform getInstance() {
        return StringEntityTransform.StringEntityTransformHolder.INSTANCE;
    }
    private StringEntityTransform() {
        super(EntityType.STRING, String.class);
    }
    @Override
    public void serializeEntity(String entity, List<Byte> bytes) {
        bytes.add(EntityType.STRING.getCode());
        writeInt(entity.length(), bytes);
        for (var b : entity.getBytes()) {
            bytes.add(b);
        }
    }

    @Override
    public String deserializeEntity(Iterator<Byte> bytes) {
        return readStr(bytes);
    }


    @Override
    public boolean canTransform(Object entity) {
        return entity instanceof String;
    }
}