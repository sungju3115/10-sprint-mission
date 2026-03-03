package com.sprint.mission.discodeit.dto.ReadStatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "읽음 상태 생성 요청")
public record ReadStatusCreateRequest(
        @Schema(description = "읽음 상태를 등록할 사용자 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @Schema(description = "읽음 상태를 등록할 채널 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @Schema(description = "마지막으로 읽은 시점", example = "2026-02-24T16:50:00Z")
        @NotNull(message = "읽은 시점 정보는 필수입니다.")
        Instant lastReadAt
) {
}