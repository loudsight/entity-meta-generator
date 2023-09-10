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
   Include the @Introspect annotation processor library in your project's dependencies.
   ```xml
   <dependency>
      <groupId>com.loudsight.meta</groupId>
      <artifactId>entity-meta-generator-lib</artifactId>
      <version>0.0.1</version>
   </dependency>
    ```

2. **Annotate Your POJO**: Annotate your Java POJO class with `@Introspect`.

    ```java
    import com.loudsight.meta.annotation.Introspect;
   
    @Introspect(clazz = Person.class)
    public static class Person {
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

@Introspect(clazz = Person.class)
public static class Person {
   private String name;
   private String age;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getAge() {
      return age;
   }

   public void setAge(String age) {
      this.age = age;
   }
   // etc
}

```

After compiling your code, the annotation processor generates a class (e.g., `PersonMeta`) that implements the `Meta<Person>` interface. You can use this generated class to access metadata about the `Person` class.

```java
        PersonMeta personMeta = PersonMeta.getInstance();

        // Get the type name, package name, and simple type name
        System.out.println("Type Name: " + personMeta.getTypeName());
        System.out.println("Package Name: " + personMeta.getPackageName());
        System.out.println("Simple Type Name: " + personMeta.getSimpleTypeName());

        // Get the type class
        Class<Person> personClass = personMeta.getTypeClass();
        System.out.println("Type Class: " + personClass.getName());

        // Get a collection of entity fields
        Collection<EntityField<Person, ?>> fields = personMeta.getFields();
        for (EntityField<Person, ?> field : fields) {
            System.out.println("Field: " + field.getName());
        }

        // Get a list of entity constructors
        List<EntityConstructor> constructors = personMeta.getConstructors();
        for (EntityConstructor constructor : constructors) {
            System.out.println("Constructor: " + constructor);
        }

        // Get a list of entity annotations
        List<EntityAnnotation> annotations = personMeta.getAnnotations();
        for (EntityAnnotation annotation : annotations) {
            System.out.println("Annotation: " + annotation.getName());
        }

        // Create a new instance of the Person class
        Person person = personMeta.newInstance();
        System.out.println("New Instance: " + person);

        // Create a new instance of the Person class with values
        Map<String, ?> values = Map.of("name", "John", "age", 30);
        Person john = personMeta.newInstance(values);
        System.out.println("New Instance with Values: " + john);

        // Get fields as a map
        Map<String, EntityField<Person, ?>> fieldMap = personMeta.getFieldAsMap();
        System.out.println("Fields as Map: " + fieldMap);

        // Get a field by name
        EntityField<Person, ?> ageField = personMeta.getFieldByName("age");
        System.out.println("Age Field: " + ageField);

        // Get the type hierarchy
        List<Class<?>> typeHierarchy = personMeta.getTypeHierarchy();
        System.out.println("Type Hierarchy: " + typeHierarchy);

        // Get a list of entity methods
        List<EntityMethod<Person, ?>> methods = personMeta.getMethods();
        for (EntityMethod<Person, ?> method : methods) {
            System.out.println("Method: " + method.name());
        }

        // Convert the Person object to a map
        Map<String, Object> personMap = personMeta.toMap(person);
        System.out.println("Person as Map: " + personMap);
```

This basic example is not exhaustive, but, demonstrates simple uses of each of the methods defined in the `Meta<T>` interface to access metadata and perform various operations on the generated `PersonMeta` class.


## Conclusion

The `@Introspect` annotation processor simplifies the generation of metadata classes for annotated POJOs, providing a convenient way to introspect and access information about these classes at runtime.

For more information and advanced usage, refer to the official documentation of the `@Introspect` annotation processor.

## Troubleshooting

If you encounter any issues or have questions while using the `@Introspect` annotation processor, you can seek assistance in the following ways:

1. **Raise an Issue**: If you believe you've found a bug, have a feature request, or need help with something specific to the annotation processor, please visit the project's [GitHub Issues Page](https://github.com/loudsight/entity-meta-generator/issues). Here, you can search for existing issues or open a new one to report your problem or request assistance. Please provide as much detail as possible to help us understand the issue.

2. **Contribute a Patch**: Contributions to the project are welcome and greatly appreciated. If you have a solution to an existing issue or want to propose an enhancement, consider contributing code to the project. Please follow the project's [contribution guidelines](https://github.com/loudsight/entity-meta-generator/blob/master/CONTRIBUTING.md) for more information on how to get started.


Feedback and contributions are welcome!

---

