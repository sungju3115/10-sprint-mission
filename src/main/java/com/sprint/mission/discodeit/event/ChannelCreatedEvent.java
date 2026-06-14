package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelCreatedEvent {
    private final ChannelDTO channelDTO;
}
