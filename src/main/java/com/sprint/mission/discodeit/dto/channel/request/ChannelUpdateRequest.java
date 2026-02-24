package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 Channel 정보")
public record ChannelUpdateRequest(
        @Schema(description = "수정할 채널 이름", example = "general2")
        String newName,
        @Schema(description = "수정할 채널 설명", example = "일반 채널2 입니다")
        String newDescription
) {
}
