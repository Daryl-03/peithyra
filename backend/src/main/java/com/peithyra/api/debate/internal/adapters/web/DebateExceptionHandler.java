package com.peithyra.api.debate.internal.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.dto.ApiErrorResponse;
import com.peithyra.api.debate.internal.application.exceptioin.DebateAlreadyOngoingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.function.EntityResponse;

@RestControllerAdvice
public class DebateExceptionHandler {

    @ExceptionHandler(DebateAlreadyOngoingException.class)
    public EntityResponse<ApiErrorResponse> handleDebateAlreadyOngoingException(DebateAlreadyOngoingException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse("DEBATE_ALREADY_ONGOING", ex.getMessage());
        return EntityResponse.fromObject(errorResponse).status(409).build();
    }
}
