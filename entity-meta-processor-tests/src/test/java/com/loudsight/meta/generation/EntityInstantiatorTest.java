package com.loudsight.meta.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityInstantiatorTest {

//    static {
//        MetaRepository.getInstance().register(SimpleEntityMeta.INSTANCE);
//    }
//
//    @Test
//    public void testNoParameters() {
//        EntityConstructor constructor = new EntityConstructor(Collections.emptyList(), objects -> new SimpleEntity());
//        var matcher = EntityInstantiator.INSTANCE;
//
//        assertEquals(new SimpleEntity(), matcher.invoke(Arrays.asList(constructor), Collections.emptyMap(), Collections.emptyMap()));
//    }
//
//    @Test
//    public void testNoArgsConstructorWithParameters() {
//        Object[] parameters = new Object[]{
//                5,
//                ItemType.PARENT,
//                100L,
//                true
//        };
//        var expected = newSimpleEntity(parameters);
//        var metaRepository = MetaRepository.getInstance();
//        var meta = metaRepository.getMeta(JvmClassHelper.toKClass(SimpleEntity.class));
//        var objectMap = meta.toMap(expected);
//        EntityConstructor constructor = new EntityConstructor(Collections.emptyList(), objects -> new SimpleEntity());
//        var matcher = EntityInstantiator.INSTANCE;
//
//        assertEquals(expected, matcher.invoke(Arrays.asList(constructor),
//                meta.getFields().stream().collect(Collectors.toMap(EntityField::getName, it -> it)),
//                objectMap));
//    }
//
//    @Disabled
//    @Test
//    public void testExactParameters() {
//        Object[] parameters = new Object[]{
//                5,
//                ItemType.PARENT,
//                100L,
//                true
//        };
//        var metaRepository = MetaRepository.getInstance();
//        var meta = metaRepository.getMeta(JvmClassHelper.toKClass(SimpleEntity.class));
//        var expected = newSimpleEntity(parameters);
//
//        var objectMap = meta.toMap(expected);
//
//        EntityConstructor constructor = new EntityConstructor(Arrays.asList(
//                newEntityParameter("field1", int.class),
//                newEntityParameter("itemType", ItemType.class),
//                newEntityParameter("id", long.class),
//                newEntityParameter("field4", boolean.class),
//                newEntityParameter("name", String.class)
//        ), this::newSimpleEntity
//        );
//
//        var matcher = EntityInstantiator.INSTANCE;
//        assertEquals(expected, matcher.invoke(Arrays.asList(constructor), Collections.emptyMap(), objectMap));
//    }
//
//    private EntityParameter newEntityParameter(String name, Class<?> type) {
//        return new EntityParameter(name, JvmClassHelper.toKClass(type));
//    }
//
//    private SimpleEntity newSimpleEntity(Object... parameters) {
//        var expected = new SimpleEntity();
//        expected.setField1((int)parameters[0]);
//        expected.setItemType(ItemType.PARENT);
//        expected.setId(100L);
//        expected.setField4(true);
//        expected.setName(null);
//
//        return expected;
//    }
}
