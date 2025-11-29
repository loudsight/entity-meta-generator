package ${packageName};

import com.loudsight.meta.EntityInstantiator;
import com.loudsight.meta.DefaultMeta;
import com.loudsight.meta.entity.EntityAnnotation;
import com.loudsight.meta.entity.EntityConstructor;
import com.loudsight.meta.entity.EntityField;
import com.loudsight.meta.entity.EntityMethod;
import com.loudsight.meta.entity.EntityParameter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;


public class ${simpleTypeName}Meta extends DefaultMeta<${simpleTypeName}> {

    private static class LazyHolder {
        static final ${simpleTypeName}Meta INSTANCE = new ${simpleTypeName}Meta();
    }

    public static ${simpleTypeName}Meta getInstance() {
        return LazyHolder.INSTANCE;
    }

    ${fields}

    private static final List<EntityField<${simpleTypeName}, ?>> _fields = List.of(
        // ${fieldList}
    );

    private static final List<EntityConstructor> constructors = List.of(
        // ${constructorList}
    );

    private static final List<EntityAnnotation> annotations = List.of(
        // ${annotationList}
    );

    private static final Map<String, EntityField<${simpleTypeName}, ?>> mapOfFields = _fields.stream().collect(
                                                                                        Collectors.toMap(
                                                                                            it -> it.getName(),
                                                                                            it -> it
                                                                                        )
                                                                                );

    private ${simpleTypeName}Meta() {
        super(
        "${packageName}.${simpleTypeName}",
                    "${simpleTypeName}",
                    ${simpleTypeName}.class,
                    ${simpleTypeName}Meta._fields,
                    ${simpleTypeName}Meta.constructors,
                    ${simpleTypeName}Meta.annotations,
                    Collections.emptyList(),
                    Collections.emptyList()
        );
    }


    @Override
    public ${simpleTypeName} newInstance( Map<String, ?> fieldMap) {
        return EntityInstantiator.getInstance().invoke(constructors, mapOfFields, fieldMap);
    }
}
