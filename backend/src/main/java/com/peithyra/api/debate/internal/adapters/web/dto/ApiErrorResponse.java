package com.peithyra.api.debate.internal.adapters.web.dto;

public record ApiErrorResponse(
        String code,
        String message
) {
}
