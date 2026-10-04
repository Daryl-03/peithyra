package com.peithyra.api.identity.internal.application.port.in;

import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.internal.application.dto.UserViewMapper;
import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import com.peithyra.api.identity.internal.domain.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserView execute(String username, String externalId){
        User newUser = new User(UUID.randomUUID(), externalId, username);
        userRepository.save(newUser);
        return UserViewMapper.toView(newUser);
    }
}
