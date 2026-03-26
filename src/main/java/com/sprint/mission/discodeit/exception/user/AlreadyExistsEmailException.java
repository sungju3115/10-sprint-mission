package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class AlreadyExistsEmailException extends UserException{
    public AlreadyExistsEmailException(String key, Object value){
        super(ErrorCode.ALREADY_EXISTS_EMAIL, Map.of(key, value));
    }
}
