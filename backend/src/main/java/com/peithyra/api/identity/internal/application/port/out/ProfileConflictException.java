package com.peithyra.api.identity.internal.application.port.out;

public class ProfileConflictException extends RuntimeException {
    public ProfileConflictException() {
        super("Username or external identity already registered");
    }
}
