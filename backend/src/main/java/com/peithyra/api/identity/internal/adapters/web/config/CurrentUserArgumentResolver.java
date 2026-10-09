package com.peithyra.api.identity.internal.adapters.web.config;

import com.peithyra.api.identity.web.exceptions.OnboardingRequiredException;
import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.GetUserUseCase;
import com.peithyra.api.identity.web.CurrentUser;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final GetUserUseCase getUserUseCase;

    public CurrentUserArgumentResolver(GetUserUseCase getUserUseCase) {
        this.getUserUseCase = getUserUseCase;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(UserView.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        if (!(webRequest.getUserPrincipal() instanceof JwtAuthenticationToken authentication) || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String externalId = authentication.getToken().getSubject();

        return getUserUseCase.execute(externalId).orElseThrow(() -> new OnboardingRequiredException("User not found. Onboarding required."));
    }

}
