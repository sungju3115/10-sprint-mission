package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class StorageFailedEvent {
    private final String title;
    private final String message;

    @JsonCreator
    public StorageFailedEvent(
            @JsonProperty("title") String title,
            @JsonProperty("message") String message
    ) {
        this.title = title;
        this.message = message;
    }
}
