package com.sprint.mission.discodeit.dto.channel.request;

public record ChannelUpdateRequest(
        String newName,
        String newDescription
) {
}
