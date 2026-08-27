package com.peithyra.api.debate.internal.application.port.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserPort {
    Map<UUID, String> findUsernamesByIds(Set<UUID> userIds);
}
