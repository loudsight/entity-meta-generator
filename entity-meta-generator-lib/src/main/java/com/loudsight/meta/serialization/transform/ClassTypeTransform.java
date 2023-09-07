package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityTransform;
import com.loudsight.meta.serialization.EntityType;

import java.util.Iterator;
import java.util.List;

public class ClassTypeTransform extends EntityTransform<Class<?>> {

private static class ClassTypeTransformHolder {
    private static final ClassTypeTransform INSTANCE = new ClassTypeTransform();
}
    // global access point
    public static ClassTypeTransform getInstance() {
        return ClassTypeTransformHolder.INSTANCE;
    }
    private ClassTypeTransform() {
        super(EntityType.CLASS, (Class<Class<?>>)(Object)Class.class);
    }

    @Override public void serializeEntity( Class<?> entity, List<Byte> bytes) {
        bytes.add(EntityType.CLASS.getCode());
        writeStr(entity.getName(), bytes);
    }

    @Override public Class<?>  deserializeEntity(Iterator<Byte> bytes) {
        var className = readStr(bytes);

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}