package com.sprint.mission.discodeit.dto.userStatus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 상태 정보 응답")
public record UserStatusResponse(
        @Schema(description = "상태 정보 고유 식별자(ID)", example = "880e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "상태 기록 생성 일시", example = "2026-01-03T10:00:00Z")
        Instant createdAt,

        @Schema(description = "상태 정보 최근 수정 일시", example = "2026-02-24T16:55:00Z")
        Instant updatedAt,

        @Schema(description = "해당 상태의 사용자 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        UUID userId,

        @Schema(description = "마지막 활동 시점", example = "2026-02-24T16:54:00Z")
        Instant lastActiveAt,

        @Schema(description = "현재 온라인 여부", example = "true")
        boolean online
) {
}