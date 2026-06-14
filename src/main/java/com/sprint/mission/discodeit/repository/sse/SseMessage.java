package com.sprint.mission.discodeit.repository.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SseMessage {
    private final UUID id;
    private final String eventName;
    private final Object data;
    private final Collection<UUID> receiverIds; // null이면 전체 broadcast
}
