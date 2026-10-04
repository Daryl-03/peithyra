package com.peithyra.api.identity.internal.adapters.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class IdentityWebConfiguration implements WebMvcConfigurer {
    private final CurrentUserArgumentResolver resolver;

    public IdentityWebConfiguration(CurrentUserArgumentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(resolver);
    }
}
