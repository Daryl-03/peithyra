package com.peithyra.api.debate.internal.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.dto.ApiErrorResponse;
import com.peithyra.api.debate.internal.application.exception.DebateAlreadyOngoingException;
import com.peithyra.api.debate.internal.application.exception.DebateNotFoundException;
import com.peithyra.api.debate.internal.domain.exceptions.DebateNotJoinableException;
import com.peithyra.api.debate.internal.domain.exceptions.DebateSideFullException;
import com.peithyra.api.debate.internal.domain.exceptions.ParticipantAlreadyJoinedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DebateExceptionHandler {

    @ExceptionHandler(DebateSideFullException.class)
    public ResponseEntity<ApiErrorResponse> handleDebateSideFullException(DebateSideFullException ex) {
        return ResponseEntity.status(409).body(new ApiErrorResponse("DEBATE_SIDE_FULL", ex.getMessage()));
    }

    @ExceptionHandler(ParticipantAlreadyJoinedException.class)
    public ResponseEntity<ApiErrorResponse> handleParticipantAlreadyJoinedException(ParticipantAlreadyJoinedException ex) {
        return ResponseEntity.status(409).body(new ApiErrorResponse("PARTICIPANT_ALREADY_JOINED", ex.getMessage()));
    }

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

    @ExceptionHandler(DebateNotJoinableException.class)
    public ResponseEntity<ApiErrorResponse> handleDebateNotJoinableException(DebateNotJoinableException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse("DEBATE_NOT_JOINABLE", ex.getMessage());
        return ResponseEntity.status(409).body(errorResponse);
    }
}
