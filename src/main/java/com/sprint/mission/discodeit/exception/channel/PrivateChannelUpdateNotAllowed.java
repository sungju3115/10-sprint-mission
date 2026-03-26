package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateNotAllowed extends ChannelException{
    public PrivateChannelUpdateNotAllowed(String key, Object value){
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, Map.of(key, value));
    }
}
