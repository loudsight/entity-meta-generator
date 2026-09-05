package com.loudsight.meta.exceptions;

public class BadAnnotationUsageException extends Exception {
    private static final long serialVersionUID = 1L;

    public BadAnnotationUsageException(String annotatedElementClassName,
                                        String annotation,
                                        String description) {
        super(String.format("Element %s is incorrectly annotated with %s: %s",
                annotatedElementClassName,
                annotation,
                description));
    }
}
