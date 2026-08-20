package com.loudsight.meta.exceptions;

public class ClassGenerationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClassGenerationException(String message) {
        super(message);
    }
    public ClassGenerationException(Throwable t) {
        super(t);
    }

}
