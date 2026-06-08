package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RoleUpdatedEvent {
    private final UUID userId;
    private final String oldRole;
    private final String newRole;

    @JsonCreator
    public RoleUpdatedEvent(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("oldRole") String oldRole,
            @JsonProperty("newRole") String newRole
    ) {
        this.userId = userId;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }
}
