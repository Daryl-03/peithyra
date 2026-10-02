package com.peithyra.api.identity.internal.application.port.in;

import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;

import java.util.Optional;

public class GetUserUseCase {
    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> execute(String externalId){
        return userRepository.findByExternalId(externalId);
    }
}
