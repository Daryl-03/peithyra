package com.peithyra.api.debate.internal.adapters;

import com.peithyra.api.debate.internal.application.port.out.UserPort;
import com.peithyra.api.identity.GetUsernamesUseCase;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class UserPortAdapter implements UserPort {
    private final GetUsernamesUseCase getUsernamesUseCase;

    public UserPortAdapter(GetUsernamesUseCase getUsernamesUseCase) {
        this.getUsernamesUseCase = getUsernamesUseCase;
    }

    @Override
    public Map<UUID, String> findUsernamesByIds(Set<UUID> userIds) {
        return getUsernamesUseCase.execute(userIds);
    }
}
