package com.loudsight.meta;

import com.loudsight.useful.helper.ClassHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for Schema instances.
 * Mirrors MetaRepository's cache shape but operates on class-free Schema objects.
 * Used by persistence-server to store schemas pushed by clients without touching the classpath.
 */
public class SchemaRepository {
    private static class SchemaRepositoryHolder {
        private static final SchemaRepository INSTANCE = new SchemaRepository();
    }

    public static SchemaRepository getInstance() {
        return SchemaRepositoryHolder.INSTANCE;
    }

    private final Map<String, Schema> schemaByTypeName = new ConcurrentHashMap<>();

    /**
     * Registers a schema explicitly (pushed by client at startup).
     * @param schema the schema to register
     */
    public void register(Schema schema) {
        schemaByTypeName.put(schema.typeName(), schema);
    }

    /**
     * Gets a schema by type name.
     * First checks the registered map, then falls back to classpath resolution
     * (for JVMs that have the generated <X>Schema class, e.g. client-side).
     * @param typeName the type name
     * @return the schema
     * @throws RuntimeException if schema is not registered and not on classpath
     */
    public Schema getSchema(String typeName) {
        Schema schema = schemaByTypeName.get(typeName);
        if (schema != null) {
            return schema;
        }

        // Fallback to classpath resolution for JVMs that have the generated Schema class
        try {
            String schemaClassName = typeName + "Schema";
            Class<?> baseClass = Class.forName(typeName);
            ClassLoader baseClassLoader = baseClass.getClassLoader();
            Class<?> schemaClass = Class.forName(schemaClassName, true, baseClassLoader);

            ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(baseClassLoader);
                var method = schemaClass.getMethod("getInstance");
                var result = method.invoke(null);
                return ClassHelper.uncheckedCast(result);
            } finally {
                Thread.currentThread().setContextClassLoader(originalContextClassLoader);
            }
        } catch (Exception e) {
            throw new RuntimeException("Schema not found for type: " + typeName + 
                ". Schema must be registered via register() or available on classpath as " + typeName + "Schema", e);
        }
    }

    /**
     * Reads all @Introspect type names from META-INF/introspect-types.index.
     * Uses getResources() (plural) to merge all JARs' index files, fixing the bug
     * where singular getResourceAsStream only returns the first matching resource.
     * @param classLoader the classloader to use
     * @return set of all introspect type names
     */
    public Set<String> readAllIntrospectTypeNames(ClassLoader classLoader) {
        Set<String> allTypes = new HashSet<>();

        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/introspect-types.index");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream is = url.openStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allTypes.add(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            // Index file not found or error reading - return empty set
        }

        return allTypes;
    }
}
