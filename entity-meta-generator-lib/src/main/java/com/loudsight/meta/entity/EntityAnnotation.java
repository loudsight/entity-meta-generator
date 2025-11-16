package com.loudsight.meta.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.loudsight.meta.annotation.Id;

/**
 * Class representing an annotation on an entity.
 */
public class EntityAnnotation {
    /**
     * Gets the properties of this annotation.
     * @return map of annotation properties
     */
    public Map<String, AnnotationValue> getProperties() {
        return properties;
    }

    /**
     * Class representing a value of an annotation property.
     */
    public static class AnnotationValue {
        @Id
        private final String name;

        private final Object value;

        /**
         * Constructs an AnnotationValue.
         * @param name the property name
         * @param value the property value
         */
        AnnotationValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Gets the property value.
         * @return the value
         */
        public Object getValue() {
            return value;
        }
    }


    /**
     * The annotation name.
     */
    @Id
    private final String name;
    /**
     * The annotation properties.
     */
    private final Map<String, AnnotationValue> properties;

    /**
     * Constructs an EntityAnnotation.
     * @param name the annotation name
     * @param annotationValues the annotation values
     */
    public EntityAnnotation(String name, AnnotationValue... annotationValues) {
        this.name = name;
        properties = Arrays.stream(annotationValues).collect(Collectors.toMap(it -> it.name, it -> it));
    }

    /**
     * Gets the annotation name.
     * @return the annotation name
     */
    public String getName() {
        return name;
    }
}