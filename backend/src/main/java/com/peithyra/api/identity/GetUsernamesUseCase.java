package com.peithyra.api.identity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GetUsernamesUseCase {
    Map<UUID, String> execute(Set<UUID> userIds);
}
