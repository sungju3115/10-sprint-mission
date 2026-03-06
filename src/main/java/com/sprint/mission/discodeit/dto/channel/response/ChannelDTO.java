package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDTO(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserDTO> participantIds,
        Instant lastMessageAt
) {
}

