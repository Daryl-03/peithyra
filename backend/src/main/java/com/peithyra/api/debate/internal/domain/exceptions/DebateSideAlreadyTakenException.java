package com.peithyra.api.debate.internal.domain.exceptions;

public class DebateSideAlreadyTakenException extends RuntimeException {
    public DebateSideAlreadyTakenException(String message) {
        super(message);
    }
}
