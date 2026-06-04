package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class RoleUpdatedEvent {
    private final UUID userId;
    private final String oldRole;
    private final String newRole;
}
