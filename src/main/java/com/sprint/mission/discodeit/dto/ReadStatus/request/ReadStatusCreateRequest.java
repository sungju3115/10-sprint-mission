package com.sprint.mission.discodeit.dto.ReadStatus.request;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadTime
) {
}
