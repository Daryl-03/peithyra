package com.peithyra.api.debate.internal.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.dto.CreateDebateRequest;
import com.peithyra.api.debate.internal.application.dto.CreateDebateResponse;
import com.peithyra.api.debate.internal.application.port.in.CreateDebateUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debates")
public class DebateController {

    private final CreateDebateUseCase createDebateUseCase;

    public DebateController(CreateDebateUseCase createDebateUseCase) {
        this.createDebateUseCase = createDebateUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateDebateResponse> createDebate(@RequestBody @Valid CreateDebateRequest request) {
        CreateDebateResponse response = createDebateUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
