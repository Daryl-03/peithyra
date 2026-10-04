package com.peithyra.api.identity.internal.application.port.in;

import com.peithyra.api.identity.GetUserUseCase;
import com.peithyra.api.identity.UserView;
import com.peithyra.api.identity.internal.application.dto.UserViewMapper;
import com.peithyra.api.identity.internal.application.port.out.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserInteractor implements GetUserUseCase {
    private final UserRepository userRepository;

    public GetUserInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserView> execute(String externalId){
        return userRepository.findByExternalId(externalId).map(UserViewMapper::toView);
    }
}
