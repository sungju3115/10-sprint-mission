package com.sprint.mission.discodeit.dto.message.request;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;

import java.util.List;

public record MessageUpdateRequest(
        String newContent,
        List<BinaryContentCreateRequest> attachments
) {
}
