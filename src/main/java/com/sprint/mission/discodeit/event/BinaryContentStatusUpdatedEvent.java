package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentStatusUpdatedEvent {
    private final BinaryContentDTO binaryContentDTO;
}
