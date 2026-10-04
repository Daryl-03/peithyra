package com.peithyra.api.identity.internal.adapters.web.dto;

public record ApiErrorResponse(
        String code,
        String message
) {
}
