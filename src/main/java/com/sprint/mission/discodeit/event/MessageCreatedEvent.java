package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageCreatedEvent {
    private final UUID messageId;
    private final UUID channelId;
    private final UUID senderId;
    private final String senderName;
    private final String channelName;
    private final String content;

    @JsonCreator
    public MessageCreatedEvent(
            @JsonProperty("messageId") UUID messageId,
            @JsonProperty("channelId") UUID channelId,
            @JsonProperty("senderId") UUID senderId,
            @JsonProperty("senderName") String senderName,
            @JsonProperty("channelName") String channelName,
            @JsonProperty("content") String content
    ) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.channelName = channelName;
        this.content = content;
    }
}
