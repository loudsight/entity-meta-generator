package com.loudsight.meta.writter;

import com.loudsight.meta.exceptions.ClassGenerationException;
import com.loudsight.meta.MetaInfo;

import javax.annotation.processing.Filer;
import java.io.IOException;

public final class MetaClassWriter extends JavaClassWriter {
    public MetaClassWriter(Filer filer) throws IOException {
        super(filer);
    }

    public void generateMetaClass(MetaInfo metaInfo)
            throws ClassGenerationException {
        this.writeFile(metaInfo);
    }

}
