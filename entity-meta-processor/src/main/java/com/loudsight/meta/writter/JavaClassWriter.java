package com.loudsight.meta.writter;

import com.loudsight.meta.exceptions.ClassGenerationException;
import com.loudsight.meta.model.MetaSourceCodeGenerator;
import com.loudsight.meta.MetaInfo;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class contains common logic to write a Java Class File usimg {@link Filer} API.
 */
public class JavaClassWriter /*<T extends GeneratedClass>*/ {

    protected final Filer filer;
    protected JavaClassWriter(Filer filer) throws IOException {
        this.filer = filer;
    }

    protected void writeFile(MetaInfo metaInfo) throws ClassGenerationException {
        try {
            MetaSourceCodeGenerator metaSourceCodeGenerator = new MetaSourceCodeGenerator(metaInfo);
            JavaFileObject generatedFile = filer.createSourceFile(metaInfo.typeName() + "Meta");
            try (PrintWriter writer = new PrintWriter(generatedFile.openWriter())) {
                TypeSpec.Builder metaClass = metaSourceCodeGenerator.generateMetaClass(metaInfo);
                JavaFile.builder(metaInfo.getPackageName(), metaClass.build())
//                .addStaticImport(Arrays.class, "asList")
//                .addImport(List.class)
                        .build()
                        .writeTo(writer);
//                writer.print(metaClass);
            }
        } catch (IOException e) {
            throw new ClassGenerationException(e);
        }

    }

}
