package com.peithyra.api.debate.internal.adapters.web.dto;


import com.peithyra.api.debate.internal.application.dto.CreateDebateCommand;
import com.peithyra.api.debate.internal.domain.DebateSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDebateRequest(
    @NotBlank
    @Size(min = 15, max = 200)
    String proposition,
    @NotBlank
    @Size(min = 15, max = 200)
    String description,
    DebateSide side
) {
    public CreateDebateCommand toCommand(){
        return new CreateDebateCommand(proposition, description, side);
    }
}
