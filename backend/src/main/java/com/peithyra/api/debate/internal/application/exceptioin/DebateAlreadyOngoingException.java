package com.peithyra.api.debate.internal.application.exceptioin;

public class DebateAlreadyOngoingException extends RuntimeException {
    public DebateAlreadyOngoingException(String message) {
        super(message);
    }
}
