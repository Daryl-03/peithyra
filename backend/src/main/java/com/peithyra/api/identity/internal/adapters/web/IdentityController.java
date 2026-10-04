package com.peithyra.api.identity.internal.adapters.web;

import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.internal.application.port.in.CreateUserUseCase;
import com.peithyra.api.identity.internal.application.port.in.GetUserUseCase;
import com.peithyra.api.identity.web.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identity")
public class IdentityController {

    private final GetUserUseCase getUserUseCase;
    private final CreateUserUseCase createUserUseCase;

    public IdentityController(GetUserUseCase getUserUseCase, CreateUserUseCase createUserUseCase) {
        this.getUserUseCase = getUserUseCase;
        this.createUserUseCase = createUserUseCase;
    }

    @GetMapping("/me")
    public UserView getCurrentUser(@CurrentUser UserView user) {
        return user;
    }

    @PostMapping("/register")
    public UserView createCurrentUser(String username, @AuthenticationPrincipal Jwt jwt) {
        return createUserUseCase.execute(username, jwt.getSubject());
    }
}
