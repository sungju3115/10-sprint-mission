package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException{
    public ChannelNotFoundException(String key, Object value){
        super(ErrorCode.CHANNEL_NOT_FOUND, Map.of(key, value));
    }
}
