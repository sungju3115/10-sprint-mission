package com.sprint.mission.discodeit.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 정보 응답")
public record UserResponse(
        @Schema(description = "사용자 고유 식별자(ID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "계정 생성 일시", example = "2026-01-03T10:00:00Z")
        Instant createdAt,

        @Schema(description = "계정 정보 수정 일시", example = "2026-02-24T15:00:00Z")
        Instant updatedAt,

        @Schema(description = "사용자 이름(닉네임)", example = "승주")
        String username,

        @Schema(description = "사용자 이메일", example = "seungju@example.com")
        String email,

        // password 필드는 보안을 위해 응답에서 제외하는 것을 권장합니다.

        @Schema(description = "프로필 이미지 정보 ID", example = "770e8400-e29b-41d4-a716-446655440000")
        UUID profileId,

        @Schema(description = "현재 접속 여부", example = "true")
        boolean online
) {}