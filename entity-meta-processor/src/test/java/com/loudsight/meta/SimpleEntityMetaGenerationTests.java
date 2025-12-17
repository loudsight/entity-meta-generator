package com.loudsight.meta;

import com.loudsight.meta.entity.SimpleClass;
import com.loudsight.meta.entity.SimpleEnum;
import com.loudsight.meta.entity.SimpleRecord;
import com.loudsight.meta.entity.EntityField;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify the annotation processor correctly generates Meta classes
 * and that MetaRepository can load and use them to introspect annotated types.
 */
public class SimpleEntityMetaGenerationTests {

    @Test
    public void verifySimpleClassMetaLoadedFromRepository() {
        // The Meta class should have been generated and MetaRepository should load it
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        
        assertNotNull(meta, "SimpleClassMeta should be generated and loadable");
        assertEquals(SimpleClass.class, meta.getTypeClass());
        assertEquals("SimpleClass", meta.getSimpleTypeName());
        assertTrue(meta.getTypeName().endsWith("SimpleClass"));
    }

    @Test
    public void verifySimpleClassMetaProvideFields() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        
        assertNotNull(meta);
        
        // Get fields from generated Meta
        Collection<EntityField<SimpleClass, ?>> fields = meta.getFields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty(), "SimpleClass should have fields");
        
        // Verify we can get specific fields
        EntityField<SimpleClass, ?> iField = meta.getFieldByName("i");
        assertNotNull(iField, "SimpleClass should have 'i' field");
        
        EntityField<SimpleClass, ?> sField = meta.getFieldByName("s");
        assertNotNull(sField, "SimpleClass should have 's' field");
    }

    @Test
    public void verifySimpleClassMetaCanConvertToMap() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        
        SimpleClass instance = new SimpleClass();
        instance.setI(42);
        instance.setS("test");
        instance.setD(3.14);
        
        // Use Meta to convert entity to map
        Map<String, Object> map = meta.toMap(instance);
        assertNotNull(map);
        assertEquals(42, map.get("i"));
        assertEquals("test", map.get("s"));
        assertEquals(3.14, map.get("d"));
    }

    @Test
    public void verifySimpleEnumMetaLoadedFromRepository() {
        // SimpleEnum is also annotated with @Introspect
        Meta<SimpleEnum> meta = MetaRepository.getInstance().getMeta(SimpleEnum.class);
        
        assertNotNull(meta, "SimpleEnumMeta should be generated and loadable");
        assertEquals(SimpleEnum.class, meta.getTypeClass());
        assertEquals("SimpleEnum", meta.getSimpleTypeName());
    }

    @Test
    public void verifySimpleRecordMetaLoadedFromRepository() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        assertNotNull(meta, "SimpleRecordMeta should be generated and loadable");
        assertEquals(SimpleRecord.class, meta.getTypeClass());
        assertEquals("SimpleRecord", meta.getSimpleTypeName());
    }

    @Test
    public void verifyRecordMetaProvideFields() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        assertNotNull(meta);
        
        // Get fields from generated Meta for record
        Collection<EntityField<SimpleRecord, ?>> fields = meta.getFields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty(), "SimpleRecord should have fields");
        
        // Verify we can get specific record fields
        EntityField<SimpleRecord, ?> iField = meta.getFieldByName("i");
        assertNotNull(iField, "SimpleRecord should have 'i' field");
        
        EntityField<SimpleRecord, ?> bField = meta.getFieldByName("b");
        assertNotNull(bField, "SimpleRecord should have 'b' field");
    }

}
