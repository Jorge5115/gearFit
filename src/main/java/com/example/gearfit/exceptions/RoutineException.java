package com.example.gearfit.exceptions;

public class RoutineException extends RuntimeException {
    public RoutineException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoutineException(String message) {
        super(message);
    }
}