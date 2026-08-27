package com.peithyra.api.debate.internal.domain.exceptions;

public class ParticipantAlreadyJoinedException extends RuntimeException {
    public ParticipantAlreadyJoinedException(String message) {
        super(message);
    }
}
