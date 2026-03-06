package com.sprint.mission.discodeit.dto.ReadStatus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "읽음 상태 상세 응답")
public record ReadStatusResponse(
        @Schema(description = "읽음 상태 고유 식별자(ID)", example = "770e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "해당 읽음 상태의 사용자 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        UUID userId,

        @Schema(description = "해당 읽음 상태의 채널 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID channelId,

        @Schema(description = "마지막으로 읽은 시점", example = "2026-02-24T17:05:00Z")
        Instant lastReadAt
) {}