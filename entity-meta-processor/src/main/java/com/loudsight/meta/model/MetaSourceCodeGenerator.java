package com.loudsight.meta.model;

import com.loudsight.meta.DefaultMeta;
import com.loudsight.meta.EntityInstantiator;
import com.loudsight.meta.MetaInfo;
import com.loudsight.meta.entity.*;
import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaSourceCodeGenerator {
    //    private static final LoggingHelper LOGGER = LoggingHelper.wrap(ApiScanner.class);
    private static final Map<String, String> typeConversions = new HashMap<>();

    static {
        typeConversions.put("Int", "Integer");
    }
    private final MetaInfo metaInfo;
    private final ClassName pojoClassType;
    private final ClassName metaClassType;
    private final TypeSpec.Builder metaClassBuilder;

    public MetaSourceCodeGenerator(MetaInfo metaInfo) throws IOException {
        this.metaInfo = metaInfo;
        this.pojoClassType = ClassName.get(metaInfo.getPackageName(), metaInfo.simpleTypeName());
        this.metaClassType = ClassName.get(metaInfo.getPackageName(), metaInfo.simpleTypeName() + "Meta");

        this.metaClassBuilder = TypeSpec
                .classBuilder(metaInfo.simpleTypeName() + "Meta")
                .superclass(ParameterizedTypeName.get(ClassName.get(DefaultMeta.class), pojoClassType))
                .addModifiers(Modifier.PUBLIC);
    }

//    class Builder {
//        private final String simpleTypeName;
//        private final String packageName;
//
//        private final TreeSet<String> imports = new TreeSet<>();
//                Builder(MetaInfo meta) {
//                    this.simpleTypeName = meta.getSimpleTypeName();
//                    this.packageName = meta.getTypeName().substring(0, meta.getTypeName().lastIndexOf('.'));
////                    this.generatedClassBuilder = FileSpec.builder(packageName, simpleTypeName);
////                    this.typeSpec = TypeSpec.objectBuilder("${simpleTypeName}Meta");
////                    this.typeClassName = new ClassName(packageName, simpleTypeName);
//                }


    //        void addTypeFields() {
////            typeSpec.addProperty(
////                PropertySpec.builder("typeName", String::class, KModifier.OVERRIDE)
////                    .getter(
////                        FunSpec.builder("get()").addStatement(
////                            "return \"${packageName}.${simpleTypeName}\""
////                        ).build()
////                    ).build())
////                .addProperty(
////                    PropertySpec.builder("typeClass", KClass::class.asTypeName().parameterizedBy(typeClassName), KModifier.OVERRIDE)
////                        .getter(
////                            FunSpec.builder("get()").addStatement(
////                                "return ${simpleTypeName}::class"
////                            ).build()
////                        ).build())
////                .addProperty(
////                    PropertySpec.builder("simpleTypeName", String::class, KModifier.OVERRIDE)
////                        .getter(
////                            FunSpec.builder("get()").addStatement(
////                                "return \"$simpleTypeName\""
////                            ).build()
////                        ).build())
////                .build()
//        }
//
    private void addConstructors() {
        var constructors = new ArrayList<String>();


        metaInfo.constructors().forEach(constructorInfo -> {
            var constructorParameters = new ArrayList<String>();
            var constructorArgsCode = new ArrayList<String>();
            List<EntityVariableInfo> constructorParameterInfo = constructorInfo.entityParameterInfos();

            IntStream.range(0, constructorParameterInfo.size()).forEach(i -> {
                var constructorParameter = constructorParameterInfo.get(i);
                String parameterTypeName = constructorParameter.getType().getTypeName();
                String entityParameter = CodeBlock.builder().addStatement(
                        String.format("new $T(\"%s\", %s.class, $T.of())",
                                constructorParameter.getName(),
                                parameterTypeName
                        ),
                        EntityParameter.class,
                        List.class
                ).build().toString().replace(";", "").trim();
                constructorParameters.add(entityParameter);
                var statement = String.format("(%s)args[%d]", parameterTypeName, i);
                constructorArgsCode.add(statement);
            });
            String constructorStr = String.format("""
                                    new $T(
                                        $T.of(%s),
                                        args -> new $T(%s)
                                    )
                            """,
                    String.join(", ", constructorParameters),
                    String.join(",", constructorArgsCode));
            var constructor = CodeBlock.builder().addStatement(
                    constructorStr,
                    EntityConstructor.class,
                    List.class,
                    pojoClassType
            );
            constructors.add(constructor.build().toString());
        });
        var constructorsInitializer = CodeBlock.builder().addStatement("$T.of(" +
                        String.join(",", constructors).replaceAll(";", "") +
                ")", List.class);

        // create the ParameterizedTypeName for List<EntityField<MyClass, ?>>
        ParameterizedTypeName listType = ParameterizedTypeName.get(
                ClassName.get(List.class),
                ClassName.get(EntityConstructor.class)
        );

        // create the FieldSpec for the fields field
        FieldSpec fieldsField = FieldSpec.builder(listType, "constructors")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(constructorsInitializer.build())
                .build();

        metaClassBuilder.addField(fieldsField);
    }

    private void addAnnotations() {
        // create the ParameterizedTypeName for List<EntityField<MyClass, ?>>
        ParameterizedTypeName listType = ParameterizedTypeName.get(
                ClassName.get(List.class),
                ClassName.get(EntityAnnotation.class)
        );

        // create the FieldSpec for the fields field
        FieldSpec fieldsField = FieldSpec.builder(listType, "annotations")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("java.util.List.of()")
                .build();

        metaClassBuilder.addField(fieldsField);

////            typeSpec.addProperty(
////                PropertySpec.builder("annotations", List::class.asTypeName().plusParameter(EntityAnnotation::class.asTypeName()), KModifier.OVERRIDE)
////                    .getter(
////                        FunSpec.builder("get()").addStatement(
////                            "return listOf()"
////                        ).build()
////                    ).build())
////                .build()
    }

//        void addMethods() {
////            var methodsSource = MutableList(meta.methods.size)  { idx ->
//////                var argsSource = mutableListOf<String>()
//////                var paramsSource = mutableListOf<String>()
//////                var method = meta.methods[idx]
//////                method.parameters.forEachIndexed { i, p ->
//////                    argsSource.add("params[$i] as ${p.type}")
//////
//////                    var type: String = getKClassFromType(p.type)
//////
//////                    paramsSource.add(
//////                        "EntityParameter(\"${p.name}\", $type)"
//////                    )
//////                }
//////                var returnType: String = getKClassFromType(method.returnType)
//////
//////                """
//////                    |    EntityMethod("${method.name}",
//////                    |        listOf(${paramsSource.joinToString(",")}),
//////                    |        $returnType, listOf(), object : EntityMethod.Invoker<$simpleTypeName, ${method.returnType}> {
//////                    |        override fun apply(instance: $simpleTypeName, vararg params: Any?): ${method.returnType}? {
//////                    |            return instance.${method.name}(${argsSource.joinToString(",")})
//////                    |        }
//////                    |    })
//////                    |""".trimMargin()
////            }
////
////            typeSpec.addProperty(
////                PropertySpec.builder("methods", List::class.asTypeName().plusParameter(EntityMethod::class.asTypeName().parameterizedBy(
////                    typeClassName, WildcardTypeName.producerOf(Any::class))), KModifier.OVERRIDE)
////                    .getter(
////                        FunSpec.builder("get()").addStatement(
////                            """
////                                return listOf(
////                                 /*${methodsSource.joinToString(",")}*/
////                                )
////                            """.trimIndent()
////                        ).build()
////                    ).build())
////                .build()
//        }
//
//        private String getKClassFromType(EntityTypeInfo type) {
//            if (type.isGeneric()) {
//                return "${type.typeName}::class as KClass<${type}>";
//            } else {
//                return "${type}::class";
//            }
//        }

    private void addFields() {
        // create the ParameterizedTypeName for List<EntityField<MyClass, ?>>
        ParameterizedTypeName listType = ParameterizedTypeName.get(
                ClassName.get(List.class),
                ParameterizedTypeName.get(
                        ClassName.get(EntityField.class),
                        pojoClassType,
                        WildcardTypeName.subtypeOf(Object.class)
                )
        );

        // create the FieldSpec for the fields field

            var fieldList = metaInfo.fields().stream().map(it -> {
                FieldSpec fieldField = newEntityFieldSpec(it);
                metaClassBuilder.addField(fieldField);

                return it.getName();
            }).collect(Collectors.joining(", "));


        FieldSpec fieldsField = FieldSpec.builder(listType, "fields")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(String.format("java.util.List.of(%s)", fieldList
                        )
                )
                .build();
        metaClassBuilder.addField(fieldsField);
    }

    TypeName entityVariableInfoToParameterizedTypeName(EntityTypeInfo info) {

        if (info.isObject()) {
            TypeMirror typeMirror = info.getType();

            if (isGeneric(typeMirror)) {
                var type = convertToJavaType(typeMirror);
                return TypeName.get(type);
            }

            return TypeName.get(typeMirror);
        } else {
            String type = getType(info);
            ClassName className = ClassName.bestGuess(type);

            return className.box();
        }

    }

        private static Type convertToJavaType(TypeMirror typeMirror) {
            if (typeMirror instanceof DeclaredType declaredType) {

                Element element = declaredType.asElement();
                Class<?> rawType = extractClass(element);
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                Type[] javaTypeArguments = new Type[typeArguments.size()];

//                for (int i = 0; i < typeArguments.size(); i++) {
//                    javaTypeArguments[i] = convertToJavaType(typeArguments.get(i));
//                }

                return ParameterizedType.class.isAssignableFrom(rawType)
                        ? createParameterizedType(rawType, javaTypeArguments)
                        : rawType;
            } else if (typeMirror instanceof ArrayType arrayType) {
                Type componentType = convertToJavaType(arrayType.getComponentType());
                return Array.newInstance((Class<?>) componentType, 0).getClass();
            } else if (typeMirror instanceof PrimitiveType primitiveType) {
                return extractPrimitiveType(primitiveType);
            }

            // Handle other cases if necessary
            throw new IllegalArgumentException("Unsupported TypeMirror: " + typeMirror);
        }

        private static Class<?> extractClass(Element element) {
            String className = element.toString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Failed to extract class: " + className, e);
            }
        }

        private static Type extractPrimitiveType(PrimitiveType primitiveType) {
            TypeKind kind = primitiveType.getKind();
            switch (kind) {
                case BOOLEAN:
                    return boolean.class;
                case BYTE:
                    return byte.class;
                case SHORT:
                    return short.class;
                case INT:
                    return int.class;
                case LONG:
                    return long.class;
                case CHAR:
                    return char.class;
                case FLOAT:
                    return float.class;
                case DOUBLE:
                    return double.class;
                default:
                    throw new IllegalArgumentException("Unsupported primitive type: " + kind);
            }
        }

        private static ParameterizedType createParameterizedType(Class<?> rawType, Type... typeArguments) {
            return new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return typeArguments.clone();
                }

                @Override
                public Type getRawType() {
                    return rawType;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            };
        }

        public static boolean isGeneric(TypeMirror typeMirror) {
            if (typeMirror.getKind() == TypeKind.DECLARED) {
                DeclaredType declaredType = (DeclaredType) typeMirror;
                for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
                    if (typeArgument.getKind() == TypeKind.TYPEVAR) {
                        return true;
                    }
                }
            }
            return false;
        }

    TypeName entityVariableInfoToParameterizedTypeNameWithoutGenericParams(EntityTypeInfo info) {

        if (info.isObject()) {
            TypeMirror typeMirror = info.getType();

//            if (isGeneric(typeMirror)) {
//                System.out.println();
//            }

            return TypeName.get(typeMirror);
        } else {
            String type = getType(info);
            ClassName className = ClassName.bestGuess(type);

            return className.box();
        }

    }

    @NotNull
    private static String getType(EntityTypeInfo info) {
        String type = info.getTypeName();
        type = typeConversions.getOrDefault(type, type);
        type = "java.lang." + type;
        return type;
    }

    private String generateEntityAnnotations(Collection<EntityAnnotationInfo> annotations) {
        return annotations.stream().map(entityAnnotation -> {
                    return CodeBlock.builder().addStatement(
                            String.format(
                                    """
                                      new $T(
                                        "%s"
                                        // todo - process value
                                      )
                                    """.stripIndent()
                                    , entityAnnotation.getName()),
                            EntityAnnotation.class).build().toString().replaceAll(";", "");
        }).collect(Collectors.joining(", "));
    }

    @NotNull
    private FieldSpec newEntityFieldSpec(EntityVariableInfo info) {
        TypeName genericType = entityVariableInfoToParameterizedTypeName(info.getType());
        ParameterizedTypeName fieldType = ParameterizedTypeName.get(
                ClassName.get(EntityField.class),
                pojoClassType,
                genericType
        );
        var annotations = generateEntityAnnotations(info.getAnnotations());

        var methodMap = metaInfo.getMethodMap();
        String setter = "(entity, value) -> { }";
        String setterName = metaInfo.isRecord()? info.getName() : getSetterName(info);

        if (!metaInfo.isRecord() && methodMap.containsKey(setterName)) {
            setter = String.format("(entity, value) -> { entity.%s(value); }", setterName);
        }

        return FieldSpec.builder(fieldType, info.getName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(
                        String.format(
                                """
                                                    new $T(
                                                        "%s",
                                                        (Class<$T>)(Object)%s.class,
                                                        %s, // isEnum,
                                                        %s, // isCollection,
                                                        java.util.List.of(%s),
                                                         entity -> entity.%s(),
                                                    %s
                                                    )
                                                        
                                        """.stripIndent(),
                                info.getName(),
                                info.getType().getTypeName(),
                                String.valueOf(info.isEnum()).toLowerCase(),
                                String.valueOf(info.isCollection()).toLowerCase(),
                                annotations,
                                metaInfo.isRecord()? info.getName() : getGetterName(info),
                                setter),
                        fieldType,
                        genericType
                )
                .build();
    }

    private String getSetterName(EntityVariableInfo info) {
        return "set" + capitaliize(info.getName().replaceFirst("is", ""));
    }

    private String capitaliize(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input as it is if it is null or empty
        }

        // Convert the first character to uppercase and concatenate it with the rest of the string
        return input.substring(0, 1).toUpperCase() + input.substring(1);

    }

    private String getGetterName(EntityVariableInfo info) {
        if (info.getType().isBoolean()) {
            return info.getName();
        } else {
            return "get" + capitaliize(info.getName());
        }
    }

    void addNewInstanceMethod() {
        ParameterizedTypeName fieldMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                WildcardTypeName.subtypeOf(Object.class)
        );
        String constructorStatement;

        if (metaInfo.isEnum()) {
            constructorStatement = String.format(
                    "return %s.valueOf((String)fieldMap.get(\"name\"))",
                    metaInfo.simpleTypeName()
            );
        } else {
            constructorStatement = CodeBlock.builder().addStatement(
                    """
                                    return $T.getInstance().invoke(constructors, getFieldAsMap(), fieldMap)
                                """, EntityInstantiator.class
            ).build().toString().replace(";", "");
        }

        var newInstanceMethod = MethodSpec.methodBuilder("newInstance")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        fieldMapType,
                        "fieldMap"
                )
                .addStatement(constructorStatement)
                .returns(pojoClassType)
                .build();
        metaClassBuilder.addMethod(newInstanceMethod);
    }
        void addClassHierarchy() {
        var listType = ParameterizedTypeName.get(
                    ClassName.get(List.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(Object.class)
                    )
            );

            var typesInHierarchy = metaInfo.typeHierarchy()
                    .stream()
                .filter(it -> it.getTypeName().contains(".") && !(it.getTypeName().startsWith("java") || it.getTypeName().startsWith("kotlin")))
                    .map(this::entityVariableInfoToParameterizedTypeName)
                    .map(it -> it.toString() + ".class")
                .collect(Collectors.joining(", "));
            var typeHierarchy = FieldSpec.builder(listType, "typeHierarchy")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer(String.format("""
                                List.of(
                                %s
                                )
                            """.stripIndent(),
                            typesInHierarchy)
                    )
//                PropertySpec.builder("typeHierarchy",
//                    List::class.asTypeName().plusParameter(KClass::class.asTypeName().plusParameter(
//                        WildcardTypeName.producerOf(Any::class.asTypeName())
//                    )), KModifier.OVERRIDE)
//                    .getter(
//                        FunSpec.builder("get()").addStatement(
//                            
//                        ).build()
//                    ).build())
                .build();
            metaClassBuilder.addField(typeHierarchy);
//
//        }
    }

    private void addConstructor() {
        var constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement(String.format("""
                                super(
                                "%s",
                                            "${simpleTypeName}",
                                            ${simpleTypeName}.class,
                                            ${simpleTypeName}Meta.fields,
                                            ${simpleTypeName}Meta.constructors,
                                            ${simpleTypeName}Meta.annotations,
                                            ${simpleTypeName}Meta.typeHierarchy,
                                            $T.emptyList()
                                );
                                    """.replaceAll("\\$\\{simpleTypeName}", metaInfo.simpleTypeName()),
                        metaInfo.getPackageName()
                ), Collections.class).build();
        metaClassBuilder.addMethod(constructor);
    }

    public TypeSpec.Builder generateMetaClass(MetaInfo meta) throws IOException {

        addSingletonInstance();
        addFields();
        addConstructors();
        addAnnotations();
        addNewInstanceMethod();
        addConstructor();

//        source.addClassDeclaration();
//        source.addTypeFields();
//        source.addMethods();
        addClassHierarchy();

        return metaClassBuilder;
    }

    private void addSingletonInstance() {
        // Create the LazyHolder class
        TypeSpec lazyHolderClass = TypeSpec.classBuilder("LazyHolder")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addField(FieldSpec.builder(metaClassType, "INSTANCE", Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", metaClassType)
                        .build())
                .build();

        metaClassBuilder.addType(lazyHolderClass)
                .addMethod(MethodSpec
                        .methodBuilder("getInstance")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(metaClassType)
                        .addStatement("return LazyHolder.INSTANCE")
                        .build());
    }
}
