package com.loudsight.meta;

import com.loudsight.meta.entity.*;
import com.loudsight.meta.serialization.EntityTransform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTest {
    static {
        MetaRepository.getInstance().register(SimpleClassMeta.getInstance());
        MetaRepository.getInstance().register(SimpleEnumMeta.getInstance());
        MetaRepository.getInstance().register(GenericTypeMeta.getInstance());
    }

    @Test
    public void testSimpleClass() {
        SimpleClass entity = new SimpleClass();
        entity.setI(30);
        entity.setS("John");
        byte[] bytes = EntityTransform.serialize(entity);
        SimpleClass deserializedEntity = EntityTransform.deserialize(bytes);

        assertEquals(entity, deserializedEntity);
    }

    @Test
    public void testSimpleEnum() {
        var one = SimpleEnum.ONE;
        byte[] bytes = EntityTransform.serialize(one);
        SimpleEnum deserializedEntity = EntityTransform.deserialize(bytes);

        assertEquals(SimpleEnum.ONE, deserializedEntity);
    }

    @Test
    public void testGenericClass() {
        var entity = new GenericType<Integer>();
        entity.setKlazz(Integer.class);
        byte[] bytes = EntityTransform.serialize(entity);
        GenericType<?> deserializedEntity = EntityTransform.deserialize(bytes);

        assertEquals(entity, deserializedEntity);
    }
}
