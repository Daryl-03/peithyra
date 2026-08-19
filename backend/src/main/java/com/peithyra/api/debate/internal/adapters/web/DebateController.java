package com.peithyra.api.debate.internal.adapters.web;

import com.peithyra.api.debate.internal.adapters.web.dto.CreateDebateRequest;
import com.peithyra.api.debate.internal.application.dto.CreateDebateResponse;
import com.peithyra.api.debate.internal.application.dto.DebateSummary;
import com.peithyra.api.debate.internal.application.dto.ListDebateCommand;
import com.peithyra.api.debate.internal.application.dto.PagedResult;
import com.peithyra.api.debate.internal.application.port.in.CreateDebateUseCase;
import com.peithyra.api.debate.internal.application.port.in.ListDebatesUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debates")
public class DebateController {

    private final CreateDebateUseCase createDebateUseCase;
    private final ListDebatesUseCase listDebatesUseCase;

    public DebateController(CreateDebateUseCase createDebateUseCase, ListDebatesUseCase listDebatesUseCase) {
        this.createDebateUseCase = createDebateUseCase;
        this.listDebatesUseCase = listDebatesUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateDebateResponse> createDebate(@RequestBody @Valid CreateDebateRequest request) {
        CreateDebateResponse response = createDebateUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResult<DebateSummary>> listDebates(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        PagedResult<DebateSummary> result = listDebatesUseCase.execute(new ListDebateCommand(page, size));
        return ResponseEntity.ok().body(result);

    }
}
