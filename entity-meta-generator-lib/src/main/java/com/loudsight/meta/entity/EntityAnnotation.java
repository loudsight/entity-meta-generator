package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Id;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityAnnotation {
    public Map<String, AnnotationValue> getProperties() {
        return properties;
    }

    public static class AnnotationValue {
        @Id
        private final String name;

        private final Object value;

        AnnotationValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }


    @Id
    private final String name;
    private final Map<String, AnnotationValue> properties;

    public EntityAnnotation(String name, AnnotationValue... annotationValues) {
        this.name = name;
        properties = Arrays.stream(annotationValues).collect(Collectors.toMap(it -> it.name, it -> it));
    }

    public String getName() {
        return name;
    }
}