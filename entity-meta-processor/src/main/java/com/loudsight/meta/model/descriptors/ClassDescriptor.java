package com.loudsight.meta.model.descriptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class ClassDescriptor {

    private String className;
    private String packageName;
    private final List<ConstructorDescriptor> constructors;
    private final List<AttributeDescriptor> attributes;
    private final List<MethodDescriptor> methods;

    public ClassDescriptor() {
        this.constructors = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public ClassDescriptor setClassName(String className) {
        this.className = className;
        return this;
    }

    public ClassDescriptor setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public ClassDescriptor addConstructor(ConstructorDescriptor constructor) {
        this.constructors.add(constructor);
        return this;
    }

    public ClassDescriptor addAttribute(AttributeDescriptor attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public ClassDescriptor addMethod(MethodDescriptor method) {
        this.methods.add(method);
        return this;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<ConstructorDescriptor> getConstructors() {
        return Collections.unmodifiableList(constructors);
    }

    public List<AttributeDescriptor> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public List<MethodDescriptor> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "class %s%s: constructors: %s, attributes: %s, methods: %s",
                this.packageName,
                this.className,
                this.constructors,
                this.attributes,
                this.methods);
    }
}
