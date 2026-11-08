package com.peithyra.api.debate.internal.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.dto.ApiErrorResponse;
import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DebateExceptionHandler {

    @ExceptionHandler(DebateAlreadyOngoingException.class)
    public ResponseEntity<ApiErrorResponse> handleDebateAlreadyOngoingException(DebateAlreadyOngoingException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse("DEBATE_ALREADY_ONGOING", ex.getMessage());
        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(DebateNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDebateNotFoundException(com.peithyra.api.debate.internal.application.exception.DebateNotFoundException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse("DEBATE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(404).body(errorResponse);
    }
}
