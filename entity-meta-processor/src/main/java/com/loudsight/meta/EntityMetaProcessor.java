package com.loudsight.meta;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.loudsight.helper.logging.LoggingHelper;
import com.loudsight.meta.annotation.Introspect;
import com.loudsight.meta.generation.MetaGeneratorService;

public class EntityMetaProcessor {

    private static final LoggingHelper logger = LoggingHelper.wrap(EntityMetaProcessor.class);

//    private final Types types;
    private final MetaGeneratorService metaGeneratorService;

    public EntityMetaProcessor(Types types, Elements elementUtils) {
//        this.types = types;
        this.metaGeneratorService = new MetaGeneratorService(types, elementUtils);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger.logInfo("processing " + annotations);
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Introspect.class);

        for (Element element : annotatedElements) {
            transformElementToModel(element, element.getAnnotation(Introspect.class));
        }
        return false;
    }

    public MetaInfo transformElementToModel(Element element, Introspect annotation) {
        logger.logInfo("processing " + element);

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
