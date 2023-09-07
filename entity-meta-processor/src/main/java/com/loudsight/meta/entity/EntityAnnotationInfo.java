package com.loudsight.meta.entity;

import com.loudsight.meta.annotation.Id;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityAnnotationInfo {
    public record AnnotationValue(@Id String name, Object value){}

    private final Map<String, AnnotationValue> properties;
    private final String name;

    public EntityAnnotationInfo(String name, AnnotationValue... annotationValues) {
        this.name = name;
        this.properties = Arrays.stream(annotationValues).collect(Collectors.toMap(AnnotationValue::name, it -> it));
    }
    public Map<String, AnnotationValue> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

}