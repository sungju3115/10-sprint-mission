package com.sprint.mission.discodeit.dto.binarycontent.response;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.util.UUID;

public record BinaryContentDTO(
        UUID id,
        String fileName,
        long size,
        String contentType,
        BinaryContentStatus status
) {
}
