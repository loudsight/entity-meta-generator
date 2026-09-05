package com.loudsight.meta.model.descriptors;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementKindVisitor8;

public final class TypeMapper extends ElementKindVisitor8<TypeMapper, Void> {

    private final ClassDescriptor classDescriptor;

    public TypeMapper() {
        this.classDescriptor = new ClassDescriptor();
    }

    @Override
    public TypeMapper visitTypeAsClass(TypeElement e, Void unused) {
        this.classDescriptor.setClassName(e.getSimpleName().toString());
        String qualifiedName = e.getQualifiedName().toString();
        this.classDescriptor.setPackageName(qualifiedName.substring(0, qualifiedName.lastIndexOf('.')));
        for (Element element : e.getEnclosedElements()) {
            analyzeElement(element);
        }
        return this;
    }

    @Override
    public TypeMapper visitTypeAsEnum(TypeElement e, Void unused) {
        return visitTypeAsClass(e, null);
    }


    @Override
    public TypeMapper visitTypeAsRecord(TypeElement e, Void unused) {
        return visitTypeAsClass(e, null);
    }

    // An if/else chain rather than a switch: the old statement switch tripped Error Prone's
    // StatementSwitchToExpressionSwitch, and the arrow form it suggests needs a `default -> { }`
    // (ElementKind has many more constants than the three handled here), which PMD then reports
    // as an empty block. Every other element kind is ignored, exactly as `default: break;` was.
    private void analyzeElement(Element e) {
        ElementKind kind = e.getKind();
        if (kind == ElementKind.CONSTRUCTOR) {
            ConstructorDescriptor constructorDescriptor = new ConstructorDescriptor(
                    this.classDescriptor.getClassName());
            for (Modifier m : e.getModifiers()) {
                constructorDescriptor.addModifier(m.name());
            }
            ExecutableElement constructorElement = (ExecutableElement) e;
            for (VariableElement parameter : constructorElement.getParameters()) {
                constructorDescriptor.addArgument(parameter.getSimpleName().toString(),
                        parameter.asType().toString());
            }
            this.classDescriptor.addConstructor(constructorDescriptor);
        } else if (kind == ElementKind.FIELD) {
            String modifier = e.getModifiers().toString();
            String name = e.getSimpleName().toString();
            String type = e.asType().toString();
            this.classDescriptor.addAttribute(new AttributeDescriptor(name, type, modifier));
        } else if (kind == ElementKind.METHOD) {
            String methodName = e.getSimpleName().toString();
            String methodReturnType = ((ExecutableElement) e).getReturnType().toString();
            MethodDescriptor methodDescriptor = new MethodDescriptor(methodName, methodReturnType);
            for (Modifier m : e.getModifiers()) {
                methodDescriptor.addModifier(m.name());
            }
            for (Element enclosed : e.getEnclosedElements()) {
                methodDescriptor.addArgument(enclosed.getSimpleName().toString(),
                        enclosed.getKind().name());
            }
            this.classDescriptor.addMethod(methodDescriptor);
        }
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }
}
