package com.loudsight.meta.serialization.transform;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

/**
 * EntityTransform implementation for String types.
 */
public final class StringEntityTransform extends EntityTransform<String> {

    /**
     * Holder for singleton instance.
     */
    private static final class StringEntityTransformHolder {
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
        // The length counts the bytes that follow (UTF-8), not characters, so the reader stays aligned.
        var utf8 = entity.getBytes(StandardCharsets.UTF_8);
        writeInt(utf8.length, bytes);
        for (var b : utf8) {
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