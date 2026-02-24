package com.sprint.mission.discodeit.dto.userStatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "사용자 상태(활동 시간) 수정 요청")
public record UserStatusUpdateRequest(
        @Schema(description = "새롭게 갱신할 마지막 활동 시점", example = "2026-02-24T16:54:00Z")
        @NotNull(message = "활동 시간 정보는 필수입니다.")
        Instant newLastActiveAt
) {
}