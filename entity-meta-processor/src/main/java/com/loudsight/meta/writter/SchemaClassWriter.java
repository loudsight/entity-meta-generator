package com.loudsight.meta.writter;

import com.loudsight.meta.exceptions.ClassGenerationException;
import com.loudsight.meta.model.SchemaSourceCodeGenerator;
import com.loudsight.meta.MetaInfo;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writer for generating <X>Schema.java classes.
 * Sibling to MetaClassWriter, using the same JavaClassWriter base.
 */
public final class SchemaClassWriter extends JavaClassWriter {
    public SchemaClassWriter(Filer filer) throws IOException {
        super(filer);
    }

    public void generateSchemaClass(MetaInfo metaInfo)
            throws ClassGenerationException {
        try {
            SchemaSourceCodeGenerator schemaSourceCodeGenerator = new SchemaSourceCodeGenerator(metaInfo);
            JavaFileObject generatedFile = filer.createSourceFile(metaInfo.typeName() + "Schema");
            try (PrintWriter writer = new PrintWriter(generatedFile.openWriter())) {
                TypeSpec.Builder schemaClass = schemaSourceCodeGenerator.generateSchemaClass();
                JavaFile.builder(metaInfo.getPackageName(), schemaClass.build())
                        .build()
                        .writeTo(writer);
            }
        } catch (IOException e) {
            throw new ClassGenerationException(e);
        }
    }
}
