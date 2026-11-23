package com.loudsight.meta.model;

import com.loudsight.meta.MetaInfo;
import com.loudsight.meta.annotation.Id;
import com.loudsight.meta.annotation.Transient;
import com.loudsight.meta.entity.EntityVariableInfo;
import com.loudsight.meta.entity.EntityTypeInfo;
import com.loudsight.meta.entity.SchemaField;
import com.loudsight.meta.Schema;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates <X>Schema.java classes from MetaInfo.
 * Emits class-free Schema objects containing only String/boolean/List data, no {@code Class<?>} references.
 * Sibling to MetaSourceCodeGenerator, consuming the same MetaInfo model.
 */
public class SchemaSourceCodeGenerator {
    private final MetaInfo metaInfo;
    private final TypeSpec.Builder schemaClassBuilder;

    public SchemaSourceCodeGenerator(MetaInfo metaInfo) {
        this.metaInfo = metaInfo;

        this.schemaClassBuilder = TypeSpec
                .classBuilder(metaInfo.simpleTypeName() + "Schema")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    /**
     * Generates the Schema class specification.
     * @return TypeSpec.Builder for the Schema class
     */
    public TypeSpec.Builder generateSchemaClass() {
        addSingletonInstance();
        addSchemaField();
        return schemaClassBuilder;
    }

    /**
     * The {@link Schema} the generated {@code <X>Schema} class holds, built here at compile time
     * from the same values the generated source is written from (see {@link #addSchemaField()}),
     * so its {@link Schema#fingerprint()} equals the one the generated class yields at runtime.
     * @return the schema of the introspected type
     */
    public Schema toSchema() {
        return new Schema(
                metaInfo.typeName(),
                metaInfo.getPackageName(),
                metaInfo.simpleTypeName(),
                metaInfo.isEnum(),
                metaInfo.isRecord(),
                schemaFields(),
                typeHierarchyNames());
    }

    private List<SchemaField> schemaFields() {
        return metaInfo.fields().stream()
                .map(SchemaSourceCodeGenerator::toSchemaField)
                .collect(Collectors.toList());
    }

    private static SchemaField toSchemaField(EntityVariableInfo field) {
        boolean isId = field.getAnnotations().stream()
                .anyMatch(it -> Id.class.getName().equals(it.getName()));
        boolean isTransient = field.getAnnotations().stream()
                .anyMatch(it -> Transient.class.getName().equals(it.getName()));
        return new SchemaField(
                field.getName(),
                field.getType().getTypeName(),
                field.isEnum(),
                field.isCollection(),
                isId,
                isTransient);
    }

    private List<String> typeHierarchyNames() {
        return metaInfo.typeHierarchy().stream()
                .map(EntityTypeInfo::getTypeName)
                .filter(it -> it.contains(".") &&
                        !(it.startsWith("java") || it.startsWith("kotlin")))
                .collect(Collectors.toList());
    }

    /**
     * Adds a static field holding the Schema instance.
     */
    private void addSchemaField() {
        // Build the list of SchemaField literals
        CodeBlock.Builder fieldsBlock = CodeBlock.builder();
        List<SchemaField> fields = schemaFields();
        for (int i = 0; i < fields.size(); i++) {
            SchemaField field = fields.get(i);
            fieldsBlock.add("new $T($S, $S, $L, $L, $L, $L)",
                    SchemaField.class,
                    field.name(),
                    field.typeName(),
                    field.isEnum(),
                    field.isCollection(),
                    field.isId(),
                    field.isTransient());
            if (i < fields.size() - 1) {
                fieldsBlock.add(",\n        ");
            }
        }

        // Build the type hierarchy list
        CodeBlock.Builder hierarchyBlock = CodeBlock.builder();
        List<String> typeHierarchy = typeHierarchyNames();
        for (int i = 0; i < typeHierarchy.size(); i++) {
            hierarchyBlock.add("$S", typeHierarchy.get(i));
            if (i < typeHierarchy.size() - 1) {
                hierarchyBlock.add(",\n        ");
            }
        }

        // Create the Schema instance as a static field
        FieldSpec schemaField = FieldSpec.builder(
                ClassName.get(Schema.class),
                "INSTANCE",
                Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
        )
                .initializer(
                        CodeBlock.builder()
                                .add("new $T(\n", Schema.class)
                                .add("    $S,\n", metaInfo.typeName())
                                .add("    $S,\n", metaInfo.getPackageName())
                                .add("    $S,\n", metaInfo.simpleTypeName())
                                .add("    $L,\n", metaInfo.isEnum())
                                .add("    $L,\n", metaInfo.isRecord())
                                .add("    $T.of(\n        ", List.class)
                                .add(fieldsBlock.build())
                                .add("\n    ),\n")
                                .add("    $T.of(\n        ", List.class)
                                .add(hierarchyBlock.build())
                                .add("\n    )\n")
                                .add(")")
                                .build()
                )
                .build();

        schemaClassBuilder.addField(schemaField);
    }

    /**
     * Adds the singleton getInstance() method.
     */
    private void addSingletonInstance() {
        MethodSpec getInstanceMethod = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(Schema.class))
                .addStatement("return INSTANCE")
                .build();

        schemaClassBuilder.addMethod(getInstanceMethod);
    }
}
