package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class AlreadyExistsNameException extends UserException{
    public AlreadyExistsNameException(String key, Object value){
        super(ErrorCode.ALREADY_EXISTS_NAME, Map.of(key, value));
    }
}
