package com.loudsight.meta.exceptions;

import java.util.Locale;

public class BadAnnotationUsageException extends Exception {
    private static final long serialVersionUID = 1L;

    public BadAnnotationUsageException(String annotatedElementClassName,
                                        String annotation,
                                        String description) {
        super(String.format(Locale.ROOT, "Element %s is incorrectly annotated with %s: %s",
                annotatedElementClassName,
                annotation,
                description));
    }
}
