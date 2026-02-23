package com.sprint.mission.discodeit.dto.channel.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        // 해당 Channel의 가장 최근 메시지 시간 정보
        // Private인 경우: User id까지 추가해서 하도록
        UUID id,
        String type,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        List<UUID> participantIds,
        Instant lastMessageAt
) {
}

