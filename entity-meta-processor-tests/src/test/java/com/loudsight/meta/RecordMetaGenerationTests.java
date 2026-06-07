package com.loudsight.meta;

import com.loudsight.meta.entity.SimpleRecord;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify Meta generation for Java records.
 *
 * These tests validate that:
 * - The annotation processor generates *Meta classes for records
 * - MetaRepository can load the generated Meta classes
 * - The Meta<T> interface provides correct record introspection
 * - Records are handled differently than regular classes (no setters, accessor methods)
 */
@DisplayName("Record Meta Generation Integration Tests")
public class RecordMetaGenerationTests {

    @Test
    @DisplayName("Record Meta class is generated and loadable from MetaRepository")
    public void recordMetaLoadedFromRepository() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        assertNotNull(meta, "SimpleRecordMeta should be generated and loadable");
        assertEquals(SimpleRecord.class, meta.getTypeClass());
        assertEquals("SimpleRecord", meta.getSimpleTypeName());
        assertTrue(meta.getTypeName().contains("SimpleRecord"));
    }

    @Test
    @DisplayName("Record Meta provides correct type information")
    public void recordMetaTypeInformation() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        assertNotNull(meta.getPackageName());
        assertEquals("SimpleRecord", meta.getSimpleTypeName());
        assertEquals(SimpleRecord.class, meta.getTypeClass());
    }

    @Test
    @DisplayName("Record Meta introspects canonical constructor parameters as fields")
    public void recordMetaFields() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        Collection<EntityField<SimpleRecord, ?>> fields = meta.getFields();
        assertNotNull(fields);
        assertEquals(2, fields.size(), "SimpleRecord should have 2 fields (i, b)");
        
        // Verify individual fields
        EntityField<SimpleRecord, ?> iField = meta.getFieldByName("i");
        assertNotNull(iField, "Should have 'i' field");
        
        EntityField<SimpleRecord, ?> bField = meta.getFieldByName("b");
        assertNotNull(bField, "Should have 'b' field");
    }

    @Test
    @DisplayName("Record Meta provides field map")
    public void recordMetaFieldMap() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        Map<String, EntityField<SimpleRecord, ?>> fieldMap = meta.getFieldAsMap();
        assertNotNull(fieldMap);
        assertTrue(fieldMap.containsKey("i"));
        assertTrue(fieldMap.containsKey("b"));
    }

    @Test
    @DisplayName("Record Meta can convert instance to map using accessor methods")
    public void recordMetaToMap() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        SimpleRecord record = new SimpleRecord(42, true);
        
        Map<String, Object> map = meta.toMap(record);
        assertNotNull(map);
        assertEquals(42, map.get("i"), "Map should contain field 'i' with value 42");
        assertEquals(true, map.get("b"), "Map should contain field 'b' with value true");
    }

    @Test
    @DisplayName("Record Meta introspects constructors")
    public void recordMetaConstructors() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        List<EntityConstructor> constructors = meta.getConstructors();
        assertNotNull(constructors);
        assertTrue(constructors.size() > 0, "Record should have constructors");
    }

    @Test
    @DisplayName("Record Meta handles different field values through toMap")
    public void recordMetaToMapVariousValues() {
        Meta<SimpleRecord> meta = MetaRepository.getInstance().getMeta(SimpleRecord.class);
        
        SimpleRecord rec1 = new SimpleRecord(Integer.MIN_VALUE, false);
        Map<String, Object> map1 = meta.toMap(rec1);
        assertEquals(Integer.MIN_VALUE, map1.get("i"));
        assertEquals(false, map1.get("b"));
        
        SimpleRecord rec2 = new SimpleRecord(Integer.MAX_VALUE, true);
        Map<String, Object> map2 = meta.toMap(rec2);
        assertEquals(Integer.MAX_VALUE, map2.get("i"));
        assertEquals(true, map2.get("b"));
    }

}
