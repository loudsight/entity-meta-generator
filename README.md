# Loudsight Entity Meta Generator

## Introduction

The Loudsight Entity Meta Generator is an annotation processor that introspects Plain Old Java Objects (POJOs) annotated with `@com.loudsight.meta.annotation.Introspect` and extracts metadata about the POJOs that'sthen used to generate classes implementing the `com.loudsight.meta.Meta<T>` interface.

## Purpose

The purpose of this annotation processor is to automate the extraction of metadata (i.e. fields, constructors, annotations, and more) from Java POJOs and generate classes that expose the metadata by implementing the `Meta<T>` interface. This generated code enables easy access during development to metadata that would otherwise have to be accessed at runtime via the Reflection API.

## Usage

To use the `@Introspect` annotation processor, follow these steps:

1. **Add the Dependency**: Ensure that you have the annotation processor library in your project's dependencies.

    ```xml
    <dependency>
        <groupId>com.loudsight.meta</groupId>
        <artifactId>entity-meta-processor</artifactId>
        <version>0.0.1</version>
        <scope>provided</scope>
    </dependency>
    ```

2. **Annotate Your POJO**: Annotate your Java POJO class with `@Introspect`.

    ```java
    import com.loudsight.meta.annotation.Introspect;

    @Introspect(clazz=MyPojo.class)
    public class MyPojo {
        // ...
    }
    ```

3. **Compile Your Code**: Use your build tool to trigger the annotation processor. Intellij and Maven will automatically detect the presence of the processor and trigger it. The generated sources will saved in ```${project.build.dir}/generated-sources/annotations``` and ```${project.build.dir}/generated-test-sources/test-annotations```.

4. **Access Generated Classes**: You can now access the generated classes that implement the `Meta<T>` interface for your annotated classes.

5. **Use Generated Metadata**: Utilize the generated metadata classes to access information about your annotated classes at runtime. You can get details like package name, simple type name, fields, constructors, annotations, and more.

## Example

Here is an example demonstrating how to use the `@Introspect` annotation processor:

```java
import com.loudsight.meta.annotation.Introspect;

@Introspect
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and setters...

    // Other methods...
}
```

After compiling your code, the annotation processor generates a class (e.g., `PersonMeta`) that implements the `Meta<Person>` interface. You can use this generated class to access metadata about the `Person` class.

```java
PersonMeta personMeta = new PersonMeta();
System.out.println("Type Name: "+personMeta.getTypeName());
        System.out.println("Package Name: "+personMeta.getPackageName());
        System.out.println("Simple Type Name: "+personMeta.getSimpleTypeName());

// Access fields, constructors, annotations, etc.
```

## Conclusion

The `@Introspect` annotation processor simplifies the generation of metadata classes for annotated POJOs, providing a convenient way to introspect and access information about these classes at runtime.

For more information and advanced usage, refer to the official documentation of the `@Introspect` annotation processor.

Certainly, here's an updated "Troubleshooting" section that directs users to the project's GitHub issues page and encourages them to raise issues or contribute:

## Troubleshooting

If you encounter any issues or have questions while using the `@Introspect` annotation processor, we encourage you to get involved in the project's development community. Here's how you can seek assistance:

1. **Raise an Issue**: If you believe you've found a bug, have a feature request, or need help with something specific to the annotation processor, please visit the project's [GitHub Issues Page](https://github.com/loudsight/entity-meta-generator/issues). Here, you can search for existing issues or open a new one to report your problem or request assistance. Please provide as much detail as possible to help us understand the issue.

2. **Contribute a Patch**: Contributions to the project are welcome and greatly appreciated. If you have a solution to an existing issue or want to propose an enhancement, consider contributing code to the project. Please follow the project's [contribution guidelines](https://github.com/loudsight/entity-meta-generator/blob/master/CONTRIBUTING.md) for more information on how to get started.


Feedback and contributions are welcome!

---

