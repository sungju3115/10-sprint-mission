package com.sprint.mission.discodeit.exception.readstatus;

public class ReadStatusNotFoundException extends ReadStatusException{
    public ReadStatusException(String key, Object value){
        super(ErrorCode.Re)
    }
}
