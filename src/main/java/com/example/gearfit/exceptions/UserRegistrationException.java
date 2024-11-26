package com.example.gearfit.exceptions;

public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje y la causa (otra excepción)
    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}