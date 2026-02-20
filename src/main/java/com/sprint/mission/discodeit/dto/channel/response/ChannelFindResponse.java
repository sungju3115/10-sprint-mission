package com.sprint.mission.discodeit.dto.channel.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindResponse(
    UUID id,
    String type,
    String name,
    String descriptions,
    List<UUID> participantsIds,
    Instant lastMessageAt
) {}
