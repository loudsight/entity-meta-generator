package com.loudsight.meta.generation;

import com.loudsight.meta.*;
import com.loudsight.meta.annotation.Transient;
import com.loudsight.meta.entity.*;
import com.loudsight.helper.ClassHelper;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor14;
import javax.lang.model.util.Types;

public class MetaGeneratorService {

    private final Map<String, MetaInfo> classToMetaMap2 = new HashMap<>();
    private final Types types;
    private final Elements elementUtils;

    public MetaGeneratorService(Types types, Elements elementUtils) {
        this.types = types;
        this.elementUtils = elementUtils;
    }

    private MetaInfo primitiveMeta(TypeElement aClass) {
        var typeName = getQualifiedName(aClass);
        var entityFieldInfos = List.of(
                new EntityVariableInfo(
                        "value",
                        new PrimitiveEntityTypeInfo(typeName, aClass.asType()),
                        aClass,
                        false,
                        false,
                        List.of()
                )
        );
        return new MetaInfo(
                typeName,
                getSimpleName(aClass),
                false,
                false,
                entityFieldInfos,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    public MetaInfo getMetaInfo(TypeElement typeElement) {
        var qualifiedName = getQualifiedName(typeElement);
        return ClassHelper.uncheckedCast(classToMetaMap2.computeIfAbsent(qualifiedName, it -> generate(typeElement)));
    }

    private String getQualifiedName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString();
    }

    private String getSimpleName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

    private MetaInfo generate(TypeElement typeElement) {
        var typeName = getQualifiedName(typeElement);
        if (isSdClass(typeName)) {
            return primitiveMeta(typeElement);
        } else {
            var metas = generate(typeElement, new HashMap<>());
            return ClassHelper.uncheckedCast(
                    metas.get(typeName)
            );
        }
    }

    private Map<String, MetaInfo> generate(
            TypeElement typeElement,
            Map<String, MetaInfo> metas
    ) {
        var meta = generateMeta(typeElement);
        var typeName = getQualifiedName(typeElement);
        metas.put(typeName, meta);
        // .map { obj -> obj.typeName }
        meta.fields()
                .stream()
                // .map { obj -> obj.typeName }
                .filter(it -> !isSdClass(it.getType().getTypeName()))
                .filter(it -> !metas.containsKey(it.getType().getTypeName()))
                .map(EntityVariableInfo::getTypeElement)
                .filter(Objects::nonNull)
                .forEach(it -> metas.put(getQualifiedName(it), generateMeta(it)));

//        meta.methods
//                .map { arrayList.of(*it.parameterType, it.returnType) }
//                .flatten()
//                .filter(!isSdClass(it) )
//                .filter(!metas.contains(it) )
//                .forEach {
//                    metas[it] = generateOneMeta(it)
//                }
        return metas;
    }

    private Boolean isSdClass(String typeName) {
        if (!typeName.contains(".")) {
            return false;
        }
        var packageName = ClassHelper.getPackageName(typeName);

        return packageName.startsWith("java.") ||
                packageName.startsWith("kotlin.");
    }

    private Boolean isObject(TypeElement type) {
        // Get the TypeElement for java.lang.Object
        var objectType = elementUtils.getTypeElement("java.lang.Object");

        return objectType.getQualifiedName().equals(type.getQualifiedName());
    }

    private MetaInfo generateMeta(TypeElement typeElement) {
        var typeName = getQualifiedName(typeElement);
        var simpleTypeName = getSimpleName(typeElement);

        Map<String, EntityVariableInfo> fields = new HashMap<>();
        Map<String, EntityMethodInfo> methods = new HashMap<>();
        List<EntityConstructorInfo> constructors = new ArrayList<>();
        var typeAnnotations = getAnnotations(() -> new ArrayList<>(typeElement.getAnnotationMirrors()));
        var classHierarchy = getClassHierarchy(typeElement);
        var isObject = !classHierarchy.isEmpty();
        var classHierarchyInfo = classHierarchy
                .stream()
                .filter(it -> !isObject(it))
                .filter(it -> !it.getQualifiedName().toString().equals(typeName))
                .map(it -> new EntityTypeInfo(getTypeName(it.getQualifiedName().toString()), isObject, it.asType()))
                .toList();
        var isEnum = classHierarchy.stream().anyMatch(it -> getQualifiedName(it).equals(Enum.class.getName()));
        var isRecord = classHierarchy.stream().anyMatch(it -> getQualifiedName(it).equals(Record.class.getName()));

        classHierarchy
                .stream()
                .filter(it -> !isObjectOrAny(it))
                .filter(it -> !isSdClass(getQualifiedName(typeElement)))
                .filter(it -> !isEnum)
                .filter(it -> typeAnnotations.stream().noneMatch(an -> an.getName().equals(Transient.class.getName())))
                .forEach(k -> {
                    // constructors
                    ElementFilter.constructorsIn(k.getEnclosedElements())
                            .stream()
                            .filter(it -> it.getEnclosingElement().equals(typeElement))
                            .forEach(constructor -> {
                                List<EntityVariableInfo> parameters = constructor.getParameters()
                                        .stream()
                                        .map(this::processVariableElements)
                                        .toList();
                                constructors.add(new EntityConstructorInfo(parameters));
                            });

                    // methods
                    ElementFilter.methodsIn(k.getEnclosedElements())
                            .stream()
                            .filter(method -> {
                                        var methodName = method.getSimpleName().toString();
                                        var declaringClassType = getDeclaringClassMethod(
                                                (TypeElement) method.getEnclosingElement(),
                                                methodName
                                        );
                                        if (declaringClassType == null)
                                            return false;
                                        var declaringClass = getQualifiedName(declaringClassType);
                                        return !(declaringClass.startsWith("java.lang.") || declaringClass.startsWith("kotlin."));
                                    }
                            )
                            .forEach(method -> {
                                var parameters = method.getParameters()
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .map(this::processVariableElements)
                                        .toList();
                                var methodAnnotations = getAnnotations(() -> new ArrayList<>(method.getAnnotationMirrors()));
                                var returnType = method.getReturnType();
                                EntityTypeInfo returnTypeName;

                                if (returnType instanceof DeclaredType declaredReturnType) {
                                    returnTypeName = getEntityType(declaredReturnType);
                                } else {
                                    returnTypeName = new PrimitiveEntityTypeInfo(getTypeName(returnType.getKind().name()), returnType);
                                }
                                var metaEntityMethodInfo = new EntityMethodInfo(
                                        method.getSimpleName().toString(),
                                        parameters,
                                        returnTypeName,
                                        methodAnnotations
                                );
                                methods.put(
                                        metaEntityMethodInfo.name() + "(" +
                                                metaEntityMethodInfo.parameters()
                                                        .stream()
                                                        .map(it -> it.getType().getTypeName() + " " + it.getType().getTypeName())
                                                        .collect(Collectors.joining("_")) + ")",
                                        metaEntityMethodInfo
                                );
                            });

                    // fields
                    var declaredFields = ElementFilter.fieldsIn(k.getEnclosedElements())
                            .stream()
                            .filter(it -> !isCompanion(it) && !it.getModifiers().contains(Modifier.STATIC))
                            .toList();
                    declaredFields.stream().sorted(Comparator.comparing(a -> a.getSimpleName().toString()))
                            .forEach(variableElement -> {
                                var metaEntityFieldInfo = processVariableElements(variableElement);
                                String getterName = metaEntityFieldInfo.getName();
                                getterName = Character.toUpperCase(getterName.charAt(0)) + getterName.substring(1);
                                var getter = methods.get("get" + getterName + "()");

                                if (getter != null) {
                                    metaEntityFieldInfo = new EntityVariableInfo(
                                            metaEntityFieldInfo.getName(),
                                            getter.returnType(),
                                            metaEntityFieldInfo.getTypeElement(),
                                            metaEntityFieldInfo.isEnum(),
                                            metaEntityFieldInfo.isCollection(),
                                            metaEntityFieldInfo.getAnnotations()
                                    );
                                }
                                fields.put(metaEntityFieldInfo.getName(), metaEntityFieldInfo);
                            });
                });

        return new MetaInfo(
                typeName,
                simpleTypeName,
                isEnum,
                isRecord,
                fields.values().stream().toList(),
                constructors,
                typeAnnotations,
                classHierarchyInfo,
                methods.values().stream().toList()
        );
    }

    SimpleTypeVisitor14<DeclaredType, Object> yy = new SimpleTypeVisitor14<>() {
        @Override
        public DeclaredType visitDeclared(DeclaredType t, Object p) {
            return t;
        }

        @Override
        public DeclaredType visitTypeVariable(TypeVariable t, Object p) {
            return ((DeclaredType) t.asElement());
        }

        @Override
        public DeclaredType visitWildcard(WildcardType t, Object p) {

            return (DeclaredType) t.getExtendsBound();
        }
    };

    private EntityTypeInfo getEntityType(DeclaredType declaredType) {
        var typeElement = ((TypeElement) declaredType.asElement());
        var qualifiedTypeName = getQualifiedName(typeElement);
        var typeName = getTypeName(qualifiedTypeName);
        var classHierarchy = getClassHierarchy(typeElement);
        var isObject = !classHierarchy.stream().toList().isEmpty();

//        if ()

        return new EntityTypeInfo(
                typeName,
//            declaredType.typeArguments
//                .mapNotNull {
//                it.accept(yy, false)
//            }
//                .map { getEntityType(it) }
//                .toList(),
                isObject,
                declaredType
        );
    }

    private EntityVariableInfo processVariableElements(VariableElement variableElement) {
        var fieldName = variableElement.getSimpleName().toString();
        var annotations = getAnnotations(() -> new ArrayList<>(variableElement.getAnnotationMirrors()));
        TypeElement typeElement = null;
        EntityTypeInfo type;

        var vType = variableElement.asType();
        if (vType instanceof DeclaredType variableElementType) {
            type = getEntityType(variableElementType);
            typeElement = (TypeElement) variableElementType.asElement();
        } else {
            if (vType instanceof PrimitiveType primitiveType) {
                type = new PrimitiveEntityTypeInfo(getTypeName(primitiveType.getKind().name()), primitiveType);
            } else {
                throw new IllegalStateException("PrimitiveType or DeclaredType expected");
            }
        }
        //qualifiedName = getTypeName(qualifiedName)
        // var type = qualifiedName
        var classes = getClassHierarchy(typeElement);
        var metaEntityFieldInfo = new EntityVariableInfo(
                fieldName,
                type,
                typeElement,
                classes.stream().anyMatch(it -> getQualifiedName(it).equals(Enum.class.getName())),
                typeElement != null && typeElement.getInterfaces().stream().anyMatch(it -> {
                    return getQualifiedName((TypeElement) ((DeclaredType) it).asElement()).equals(Collection.class.getName());
                }),
                annotations
        );
        return metaEntityFieldInfo;
    }

    private List<EntityAnnotationInfo> getAnnotations(Supplier<List<AnnotationMirror>> annotationSupplier) {
        List<EntityAnnotationInfo> annotations = new ArrayList<>();
        for (var declaredAnnotation : annotationSupplier.get()) {
            var name = getQualifiedName((TypeElement) declaredAnnotation.getAnnotationType().asElement());
            if ("kotlin.Metadata".equals(name)) {
                continue;
            }
            var annotationValues = declaredAnnotation.getElementValues()
                    .entrySet()
                    .stream()
//                .filter {
//                    it.name.startsWith(name)
//                }
                    .map(it -> {
                        return new EntityAnnotationInfo.AnnotationValue(it.getKey().getSimpleName().toString(), it.getValue().getValue());
                    }).toList()
                    .toArray(new EntityAnnotationInfo.AnnotationValue[0]);
            annotations.add(new EntityAnnotationInfo(name, annotationValues));
        }
        return annotations;
    }

    private Boolean isCompanion(VariableElement field) {
        return field != null && field == null;
        // return field.type.name.endsWith("\$Companion")
    }

    //
//    private (T, Object) -> Unit <T> getSetter(methods: Map<String, EntityMethodInfo>, field: Field){
//        var fieldName = field.name
//        if (field.type.isAssignableFrom(Boolean.class)) {
//            if (fieldName.startsWith("is")) {
//                fieldName = fieldName.replace("is", "")
//            }
//        }
//        var entityMethod = methods["set" + fieldName.replaceFirstChar { it.uppercaseChar() } + "(" + field.type.name + ")"]
//        return { entity: T, value: Object ->
//            try {
//                if (entityMethod == null) {
//                    field.isAccessible = true
//                    var fieldValue = field[entity]
//                    if (fieldValue is MutableCollection<*> && value is MutableCollection<*>) {
//                        fieldValue.addAll(ClassHelper.uncheckedCast(value))
//                    } else {
//                        field[entity] = value
//                    }
//                } else {
//                    entityMethod.invoke(entity, value)
//                }
//            } catch (e: Exception) {
//                ExceptionHelper.uncheckedThrow<RuntimeException>(e)
//            }
//        }
//    }
//
//
//    private T) -> Object <T> getGetter(methods: Map<String, EntityMethodInfo>, field: Field): (entity{
//        var fieldName = field.name
//        var String  methodName = "get" + fieldName.replaceFirstChar { it.uppercaseChar() } + "()"
//        if (field.type.isAssignableFrom(Boolean.class)) {
//            if (fieldName.startsWith("is")) {
//                methodName = fieldName
//            }
//        }
//        var entityMethod = methods[methodName]
//        return if (entityMethod == null) {
//            { entity ->
//                var fieldValueGetter = FieldValueGetter(field)
//                fieldValueGetter.getFieldValue(entity)
//
//            }
//        } else {
//            { entity ->  entityMethod.invoke(entity) }
//
//        }
//    }
    private Boolean isObjectOrAny(TypeElement typeElement) {
        return Objects.equals(getQualifiedName(typeElement), "java.lang.Object") ||
                Objects.equals(getQualifiedName(typeElement), "kotlin.Any");
    }

    private List<TypeElement> getClassHierarchy(TypeElement aClass) {
        var classes = new ArrayList<TypeElement>();

        if (aClass == null) {
            return classes;
        }
        classes.add(aClass);

        var typeElement = aClass.getSuperclass();

        if (typeElement instanceof DeclaredType) {
            classes.addAll(getClassHierarchy((TypeElement) ((DeclaredType) typeElement).asElement()));
        }
        aClass.getInterfaces().forEach(it -> {
            classes.addAll(getClassHierarchy((TypeElement) ((DeclaredType) it).asElement()));
        });

        return classes;
    }

    private TypeElement getDeclaringClassMethod(TypeElement theClass, String methodName) {
        TypeElement retClass = null;
        if (theClass.getKind() == ElementKind.CLASS) {
            var superClass = theClass.getSuperclass();
            if (superClass.getKind() != TypeKind.NONE) retClass = getDeclaringClassMethod(
                    (TypeElement) ((DeclaredType) superClass).asElement(),
                    methodName);
        }
        if (retClass == null) {
            for (var interfaceType : theClass.getInterfaces()) {
                retClass = getDeclaringClassMethod((TypeElement) ((DeclaredType) interfaceType).asElement(), methodName);
            }
        }
        if (retClass == null) {
            Collection<ExecutableElement> methods = ElementFilter.methodsIn(theClass.getEnclosedElements());
            for (var method : methods) {
                if (method.getSimpleName().toString().equals(methodName)) {
                    retClass = theClass;
                    break;
                }
            }
        }
        return retClass;
    }

    private String getTypeName(String name) {
        var typeName = name;
        var pos = typeName.indexOf("java.util.");
        if (pos == 0 && typeName.indexOf(".", pos + "java.util.".length()) == -1) {
            typeName = name.replace("java.util.", "");
        }

        pos = typeName.indexOf("java.lang.");
        if (pos == 0 && typeName.indexOf(".", pos + "java.lang.".length()) == -1) {
            typeName = name.replace("java.lang.", "");
        }
        return switch (typeName.toUpperCase()) {
            case "INT" -> "Integer";
            case "BOOLEAN" -> "Boolean";
            case "LONG" -> "Long";
            case "DOUBLE" -> "Double";
            case "FLOAT" -> "Float";
            case "VOID" -> "void";
            case "OBJECT" -> "Object";
            default -> typeName;
        };
    }

    public Types getTypes() {
        return types;
    }
}
