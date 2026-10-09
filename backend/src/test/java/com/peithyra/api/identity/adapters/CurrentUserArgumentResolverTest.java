package com.peithyra.api.identity.adapters;

import com.peithyra.api.identity.GetUserUseCase;
import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.web.CurrentUser;
import com.peithyra.api.identity.web.exceptions.OnboardingRequiredException;
import com.peithyra.api.identity.internal.adapters.web.config.CurrentUserArgumentResolver;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserArgumentResolverTest {
    private final GetUserUseCase getUser = mock(GetUserUseCase.class);
    private final CurrentUserArgumentResolver resolver = new CurrentUserArgumentResolver(getUser);
    private final NativeWebRequest request = mock(NativeWebRequest.class);
    void parameters(@CurrentUser UserView user, UserView plain, @CurrentUser String wrongType) {}

    @Test void supportsOnlyAnnotatedUserView() throws Exception {
        var method = getClass().getDeclaredMethod("parameters", UserView.class, UserView.class, String.class);
        assertTrue(resolver.supportsParameter(new MethodParameter(method, 0)));
        assertFalse(resolver.supportsParameter(new MethodParameter(method, 1)));
        assertFalse(resolver.supportsParameter(new MethodParameter(method, 2)));
    }
    @Test void rejectsMissingWrongAndUnauthenticatedPrincipals() {
        var token = token();
        token.setAuthenticated(false);
        for (Principal principal : new Principal[]{null, () -> "other", token}) {
            when(request.getUserPrincipal()).thenReturn(principal);
            var error = assertThrows(ResponseStatusException.class,
                    () -> resolver.resolveArgument(null, null, request, null));
            assertEquals(401, error.getStatusCode().value());
        }
        verifyNoInteractions(getUser);
    }
    @Test void resolvesBySubject() throws Exception {
        var user = new UserView(UUID.randomUUID(), "Alice", "kinde-1");
        when(request.getUserPrincipal()).thenReturn(token());
        when(getUser.execute("kinde-1")).thenReturn(Optional.of(user));
        assertSame(user, resolver.resolveArgument(null, null, request, null));
    }
    @Test void requiresOnboardingWhenMissing() {
        when(request.getUserPrincipal()).thenReturn(token());
        when(getUser.execute("kinde-1")).thenReturn(Optional.empty());
        assertThrows(OnboardingRequiredException.class, () -> resolver.resolveArgument(null, null, request, null));
    }
    private JwtAuthenticationToken token() {
        return new JwtAuthenticationToken(Jwt.withTokenValue("test").header("alg", "RS256")
                .subject("kinde-1").build(), List.of());
    }
}
