package com.jobhunt.exception;

public class RepeatApplicationException extends RuntimeException {
    public RepeatApplicationException(String message) {
        super(message);
    }
}
