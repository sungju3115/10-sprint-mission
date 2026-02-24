package com.sprint.mission.discodeit.dto.ReadStatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "읽음 상태 수정 요청")
public record ReadStatusUpdateRequest(
        @Schema(description = "새롭게 갱신할 마지막 읽은 시점", example = "2026-02-24T17:00:00Z")
        @NotNull(message = "갱신할 시간 정보는 필수입니다.")
        Instant newLastReadAt
) {
}