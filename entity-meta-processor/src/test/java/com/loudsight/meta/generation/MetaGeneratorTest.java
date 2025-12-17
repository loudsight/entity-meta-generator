package com.loudsight.meta.generation;

import com.loudsight.meta.Meta;
import com.loudsight.meta.MetaRepository;
import com.loudsight.meta.entity.SimpleClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify Meta generation supports:
 * - Creating instances via newInstance()
 * - Converting entities to maps via toMap()
 * - Creating instances from maps (round-trip conversion)
 * - Handling null values in maps
 */
@DisplayName("Meta Generator Functionality Tests")
public class MetaGeneratorTest {

    @Test
    @DisplayName("Meta can create instance with no arguments using default constructor")
    public void createUsingNoArgs() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        assertNotNull(meta, "Meta should be available for SimpleClass");

        SimpleClass instance = meta.newInstance();
        assertNotNull(instance);
        assertEquals(SimpleClass.class, instance.getClass());
    }

    @Test
    @DisplayName("Meta can create instance from map with all fields populated")
    public void createSimpleClassWithAllArgs() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        assertNotNull(meta);

        // Create an expected instance with populated fields
        SimpleClass expected = new SimpleClass();
        expected.setI(42);
        expected.setD(3.14);
        expected.setS("Test String");

        // Convert to map
        Map<String, Object> objectMap = meta.toMap(expected);
        assertNotNull(objectMap);
        assertEquals(42, objectMap.get("i"));
        assertEquals(3.14, objectMap.get("d"));
        assertEquals("Test String", objectMap.get("s"));

        // Create new instance from map
        SimpleClass actual = meta.newInstance(objectMap);
        assertNotNull(actual);
        assertEquals(SimpleClass.class, actual.getClass());
        assertEquals(expected, actual, "Should be able to create entity from map");
    }

    @Test
    @DisplayName("Meta can create instance from map with missing (null) fields")
    public void createSimpleClassWithMissingArgs() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        assertNotNull(meta);

        // Create instance with only some fields set
        SimpleClass expected = new SimpleClass();
        expected.setI(100);
        expected.setD(2.71);
        // Leave s as null

        Map<String, Object> objectMap = meta.toMap(expected);
        assertNotNull(objectMap);
        assertEquals(100, objectMap.get("i"));
        assertEquals(2.71, objectMap.get("d"));
        // s is null, may or may not be in map depending on implementation

        SimpleClass actual = meta.newInstance(objectMap);
        assertNotNull(actual);
        assertEquals(SimpleClass.class, actual.getClass());
        assertEquals(expected, actual, "Should handle null fields correctly");
    }

    @Test
    @DisplayName("Meta round-trip: object -> map -> object preserves data")
    public void roundTripConversion() {
        Meta<SimpleClass> meta = MetaRepository.getInstance().getMeta(SimpleClass.class);
        assertNotNull(meta);

        SimpleClass original = new SimpleClass();
        original.setI(777);
        original.setD(9.99);
        original.setS("Round Trip Test");

        // Convert to map
        Map<String, Object> map = meta.toMap(original);

        // Convert back from map
        SimpleClass restored = meta.newInstance(map);

        // Verify all fields match
        assertEquals(original.getI(), restored.getI());
        assertEquals(original.getD(), restored.getD());
        assertEquals(original.getS(), restored.getS());
        assertEquals(original, restored);
    }

}
