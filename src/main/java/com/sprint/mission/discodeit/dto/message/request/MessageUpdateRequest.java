package com.sprint.mission.discodeit.dto.message.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "메시지 수정 요청")
public record MessageUpdateRequest(
        @Schema(description = "수정할 새로운 메시지 내용", example = "수정된 메시지 내용입니다.")
        @NotBlank(message = "수정할 내용은 비어있을 수 없습니다.")
        String newContent
) {
}