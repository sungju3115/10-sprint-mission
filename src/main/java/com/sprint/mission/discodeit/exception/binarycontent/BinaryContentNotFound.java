package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentNotFound extends BinaryContentException{
    public BinaryContentNotFound(String key, Object value){
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of(key, value));
    }
}
