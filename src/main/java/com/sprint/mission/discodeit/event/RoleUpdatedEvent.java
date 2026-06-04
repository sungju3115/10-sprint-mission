package com.sprint.mission.discodeit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RoleUpdatedEvent {
    private final UUID userId;
    private final String oldRole;
    private final String newRole;
}
