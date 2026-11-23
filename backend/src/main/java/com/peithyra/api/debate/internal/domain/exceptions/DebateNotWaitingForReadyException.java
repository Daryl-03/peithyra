package com.peithyra.api.debate.internal.domain.exceptions;

public class DebateNotWaitingForReadyException extends RuntimeException {
    public DebateNotWaitingForReadyException(String message) {
        super(message);
    }
}
