package com.loudsight.meta;

import com.loudsight.meta.entity.SchemaField;
import com.loudsight.meta.fixtures.processor.SimpleClass;
import com.loudsight.meta.fixtures.processor.SimpleRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The processor writes {@code META-INF/introspect-schemas.tsv} at compile time (here, while
 * compiling this module's test fixtures): one {@code <type>\t<fingerprint>\t<field names>} line per
 * {@code @Introspect} type. The field names must be the runtime schema's, sorted and comma-joined.
 * Each fingerprint must equal, byte for byte, the
 * {@link Schema#fingerprint()} of the schema the runtime resolves for that type - the generated
 * {@code <X>Schema} that {@link SchemaRepository#getSchema} loads and a persistence client registers.
 */
class IntrospectSchemasFileTest {

    private static final String SCHEMAS_FILE = "META-INF/introspect-schemas.tsv";

    /** Reads a resource from this module's test-classes (first on the test classpath). */
    private static String read(String name) throws IOException {
        try (InputStream in = IntrospectSchemasFileTest.class.getResourceAsStream("/" + name)) {
            assertNotNull(in, name + " was not written by the processor");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String[]> rowsFromFile() throws IOException {
        var text = read(SCHEMAS_FILE);
        assertTrue(text.endsWith("\n"), "every line is newline-terminated");
        var byType = new LinkedHashMap<String, String[]>();
        for (String line : text.split("\n", -1)) {
            if (line.isEmpty()) {
                continue;
            }
            String[] columns = line.split("\t", -1);
            assertEquals(3, columns.length, "three tab-separated columns: " + line);
            byType.put(columns[0], columns);
        }
        return byType;
    }

    private static Map<String, String> fingerprintsFromFile() throws IOException {
        var byType = new LinkedHashMap<String, String>();
        rowsFromFile().forEach((typeName, columns) -> byType.put(typeName, columns[1]));
        return byType;
    }

    /** The third column parsed back: comma-separated field names, none for an empty column. */
    private static List<String> fieldNamesColumn(String column) {
        return column.isEmpty() ? List.of() : List.of(column.split(",", -1));
    }

    /**
     * The deployed field names a persistence server derives from a registered schema: the
     * {@code name()} of each {@link Schema#fields()} entry (what it logs as "Deployed fields").
     */
    private static List<String> runtimeFieldNames(String typeName) {
        return SchemaRepository.getInstance().getSchema(typeName).fields().stream()
                .map(SchemaField::name)
                .sorted()
                .toList();
    }

    @Test
    void listsEveryIntrospectedTypeOnceInSortedOrder() throws IOException {
        var fromFile = fingerprintsFromFile();
        var indexed = new ArrayList<String>();
        read("META-INF/introspect-types.index").lines()
                .filter(line -> !line.isBlank())
                .forEach(line -> indexed.add(line.trim()));
        indexed.sort(null);

        assertEquals(indexed, List.copyOf(fromFile.keySet()));
        assertTrue(fromFile.containsKey(SimpleClass.class.getName()));
        assertTrue(fromFile.containsKey(SimpleRecord.class.getName()));
    }

    @Test
    void fingerprintEqualsTheRuntimeSchemaFingerprint() throws IOException {
        var fromFile = fingerprintsFromFile();
        assertTrue(fromFile.size() >= 2, "fixtures produce at least two entries: " + fromFile);
        fromFile.forEach((typeName, compileTimeFingerprint) -> {
            String runtimeFingerprint = SchemaRepository.getInstance().getSchema(typeName).fingerprint();
            assertEquals(64, compileTimeFingerprint.length(), typeName);
            assertEquals(runtimeFingerprint, compileTimeFingerprint, typeName);
        });
    }

    @Test
    void fieldNamesEqualTheRuntimeSchemaFieldNamesSortedAndCommaJoined() throws IOException {
        var rows = rowsFromFile();
        assertTrue(rows.size() >= 2, "fixtures produce at least two entries: " + rows.keySet());
        rows.forEach((typeName, columns) -> {
            List<String> fromFile = fieldNamesColumn(columns[2]);
            assertEquals(runtimeFieldNames(typeName), fromFile, typeName);
            assertEquals(String.join(",", runtimeFieldNames(typeName)), columns[2], typeName);
        });
        assertTrue(!fieldNamesColumn(rows.get(SimpleClass.class.getName())[2]).isEmpty(),
                "a fixture with fields lists them");
    }
}
