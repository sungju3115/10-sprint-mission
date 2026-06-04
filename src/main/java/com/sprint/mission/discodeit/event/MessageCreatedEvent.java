package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final UUID senderId;
    private final String senderName;
    private final String channelName;
    private final String content;
}
