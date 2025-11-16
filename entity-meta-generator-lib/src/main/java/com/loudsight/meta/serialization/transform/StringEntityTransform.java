package com.loudsight.meta.serialization.transform;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

/**
 * EntityTransform implementation for String types.
 */
public class StringEntityTransform extends EntityTransform<String> {

    /**
     * Holder for singleton instance.
     */
    private static class StringEntityTransformHolder {
        private static final StringEntityTransform INSTANCE = new StringEntityTransform();
    }
    /**
     * Returns singleton instance of StringEntityTransform.
     * @return the singleton instance
     */
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
        for (var b : entity.getBytes(Charset.defaultCharset())) {
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