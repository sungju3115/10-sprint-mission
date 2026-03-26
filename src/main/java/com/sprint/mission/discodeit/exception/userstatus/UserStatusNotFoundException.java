package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException{
    public UserStatusNotFoundException(UUID userStatusId){
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userStatusId", userStatusId));
    }

    private UserStatusNotFoundException(String key, UUID value){
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of(key, value));
    }

    public static UserStatusNotFoundException byUserId(UUID userId){
        return new UserStatusNotFoundException("userId", userId);
    }
}
