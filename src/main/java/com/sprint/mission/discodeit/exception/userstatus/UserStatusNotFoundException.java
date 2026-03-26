package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserStatusNotFoundException extends UserStatusException{
    public UserStatusNotFoundException(String key, Object value){
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of(key, value));
    }
}
