package com.peithyra.api.config;

import com.peithyra.api.identity.GetUserUseCase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class SecurityConfig {

    private final GetUserUseCase getUserUseCase;

    public SecurityConfig(GetUserUseCase getUserUseCase) {
        this.getUserUseCase = getUserUseCase;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain
}
