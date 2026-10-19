package com.peithyra.api.identity.internal.application.port.in;

import com.peithyra.api.identity.GetUsernamesUseCase;
import com.peithyra.api.identity.internal.application.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class GetUsernamesInteractor implements GetUsernamesUseCase {
    private final UserRepository userRepository;

    public GetUsernamesInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<UUID, String> execute(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findUsernamesByIds(userIds);
    }
}
