package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class NotPrivateChannelMemberException extends ChannelException{
    public NotPrivateChannelMemberException(String key, Object value){
        super(ErrorCode.NOT_PRIVATE_CHANNEL_MEMBER_EXCEPTION, Map.of(key, value));
    }
}
