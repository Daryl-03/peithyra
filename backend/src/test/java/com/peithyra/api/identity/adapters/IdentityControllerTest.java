package com.peithyra.api.identity.adapters;

import com.peithyra.api.config.security.SecurityConfig;
import com.peithyra.api.identity.GetUserUseCase;
import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.internal.adapters.web.IdentityController;
import com.peithyra.api.identity.internal.adapters.web.config.CurrentUserArgumentResolver;
import com.peithyra.api.identity.internal.adapters.web.config.IdentityWebConfiguration;
import com.peithyra.api.identity.internal.adapters.web.exceptions.IdentityExceptionHandler;
import com.peithyra.api.identity.internal.application.port.in.CreateUserUseCase;
import com.peithyra.api.identity.internal.application.port.out.ProfileConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IdentityController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class, IdentityWebConfiguration.class, CurrentUserArgumentResolver.class, IdentityExceptionHandler.class})
class IdentityControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean JwtDecoder decoder;
    @MockitoBean GetUserUseCase getUser;
    @MockitoBean CreateUserUseCase createUser;
    private final UserView user = new UserView(UUID.randomUUID(), "Alice", "kinde-1");

    @Test void rejectsAnonymousRequests() throws Exception {
        mvc.perform(get("/identity/me")).andExpect(status().isUnauthorized());
        mvc.perform(post("/identity/register").param("username", "Alice")).andExpect(status().isUnauthorized());
        verifyNoInteractions(getUser, createUser);
    }
    @Test void returnsProfileWithSingleLookup() throws Exception {
        when(getUser.execute("kinde-1")).thenReturn(Optional.of(user));
        mvc.perform(get("/identity/me").with(jwt().jwt(j -> j.subject("kinde-1"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.id().toString()))
                .andExpect(jsonPath("$.username").value("Alice"));
        verify(getUser).execute("kinde-1");
        verifyNoMoreInteractions(getUser);
        verifyNoInteractions(createUser);
    }
    @Test void requiresOnboardingForMissingProfile() throws Exception {
        when(getUser.execute("kinde-1")).thenReturn(Optional.empty());
        mvc.perform(get("/identity/me").with(jwt().jwt(j -> j.subject("kinde-1"))))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ONBOARDING_REQUIRED"));
        verifyNoInteractions(createUser);
    }
    @Test void registersUsingJwtSubjectInsteadOfFormIdentity() throws Exception {
        when(createUser.execute("Alice", "kinde-1")).thenReturn(user);
        mvc.perform(post("/identity/register").with(jwt().jwt(j -> j.subject("kinde-1")))
                .contentType("application/x-www-form-urlencoded").param("username", "Alice")
                .param("externalId", "attacker-chosen-id"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.id().toString()));
        verify(createUser).execute("Alice", "kinde-1");
        verifyNoInteractions(getUser);
    }
    @Test void returnsConflictForDuplicateProfile() throws Exception {
        when(createUser.execute("Alice", "kinde-1")).thenThrow(new ProfileConflictException());
        mvc.perform(post("/identity/register").with(jwt().jwt(j -> j.subject("kinde-1")))
                .param("username", "Alice"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PROFILE_CONFLICT"));
    }
}
