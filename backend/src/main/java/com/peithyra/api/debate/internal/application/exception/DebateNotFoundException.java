package com.peithyra.api.debate.internal.application.exception;

public class DebateNotFoundException extends RuntimeException {
    public DebateNotFoundException(String message) {
        super(message);
    }
}
