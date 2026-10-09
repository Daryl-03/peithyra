package com.peithyra.api.identity;



import java.util.UUID;

public record UserView(
        UUID id,
        String username,
        String externalId
) {

}
