package com.loudsight.meta;

import com.loudsight.meta.annotation.Introspect;
import com.loudsight.meta.generation.MetaGeneratorService;
import org.slf4j.Logger;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

import static org.slf4j.LoggerFactory.*;

public class EntityMetaProcessor {

    private static final Logger logger = getLogger(EntityMetaProcessor.class);

//    private final Types types;
    private final MetaGeneratorService metaGeneratorService;

    public EntityMetaProcessor(Types types, Elements elementUtils) {
//        this.types = types;
        this.metaGeneratorService = new MetaGeneratorService(types, elementUtils);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (logger.isInfoEnabled()) {
            logger.info("processing " + annotations);
        }
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Introspect.class);

        for (Element element : annotatedElements) {
            transformElementToModel(element, element.getAnnotation(Introspect.class));
        }
        return false;
    }

    public MetaInfo transformElementToModel(Element element, Introspect annotation) {
        if (logger.isInfoEnabled()) {
            logger.info("processing " + element);
        }

        try {
            annotation.clazz();
        } catch (MirroredTypeException mte) {
            DeclaredType typeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement typeElement = (TypeElement) typeMirror.asElement();

            return metaGeneratorService.getMetaInfo(typeElement);
        }
        throw new RuntimeException("jjjh ");
    }

    public void finalizeElementProcessing(MetaInfo model) {
    }
}
