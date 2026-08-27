package com.peithyra.api.debate.internal.adapters;

import com.peithyra.api.debate.internal.application.port.out.UserPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StubUserPortAdapter implements UserPort {
    @Override
    public Map<UUID, String> findUsernamesByIds(Set<UUID> userIds) {
        return userIds.stream()
                .collect(
                        Collectors.toMap(
                                userId -> userId,
                                userId -> "username"
                        )
                );
    }
}
