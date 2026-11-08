package com.peithyra.api.debate.internal.application.exception;

public class DebateAlreadyOngoingException extends RuntimeException {
    public DebateAlreadyOngoingException(String message) {
        super(message);
    }
}
