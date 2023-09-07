package com.loudsight.meta.exceptions;

public class ClassGenerationException extends RuntimeException {
    public ClassGenerationException(String message) {
        super(message);
    }
    public ClassGenerationException(Throwable t) {
        super(t);
    }

}
