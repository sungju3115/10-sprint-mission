package com.sprint.mission.discodeit.exception.login;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidPasswordException extends LoginException{
    public InvalidPasswordException(String key, Object value){
        super(ErrorCode.INVALID_PASSWORD, Map.of(key, value));

    }
}
