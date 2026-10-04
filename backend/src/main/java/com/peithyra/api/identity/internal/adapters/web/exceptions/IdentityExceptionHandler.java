package com.peithyra.api.identity.internal.adapters.web.exceptions;


import com.peithyra.api.identity.internal.adapters.web.dto.ApiErrorResponse;
import com.peithyra.api.identity.web.exceptions.OnboardingRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IdentityExceptionHandler {

    @ExceptionHandler(OnboardingRequiredException.class)
    public ResponseEntity<ApiErrorResponse> handleOnboardingRequiredException(OnboardingRequiredException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErrorResponse("ONBOARDING_REQUIRED", ex.getMessage()));
    }
}
