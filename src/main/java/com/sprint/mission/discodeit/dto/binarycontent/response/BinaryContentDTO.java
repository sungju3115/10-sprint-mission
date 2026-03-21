package com.sprint.mission.discodeit.dto.binarycontent.response;

import java.util.UUID;

public record BinaryContentDTO(
        UUID id,
        String fileName,
        long size,
        String contentType
) {
}
