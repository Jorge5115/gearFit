package com.example.gearfit.exceptions;

public class UserDeleteException extends RuntimeException{
    public UserDeleteException(String message) {
        super(message);
    }
}