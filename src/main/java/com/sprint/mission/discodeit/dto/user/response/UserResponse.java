package com.sprint.mission.discodeit.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 정보 응답")
public record UserResponse(
        @Schema(description = "사용자 고유 식별자(ID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "생성 일시", example = "2026-01-03T10:00:00Z")
        Instant createdAt,

        @Schema(description = "수정 일시", example = "2026-02-24T15:00:00Z")
        Instant updatedAt,

        @Schema(description = "사용자 이름", example = "승주")
        String username,

        @Schema(description = "사용자 이메일", example = "seungju@example.com")
        String email,

        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "프로필 ID", example = "770e8400-e29b-41d4-a716-446655440000")
        UUID profileId,

        @Schema(description = "온라인 여부", example = "true")
        boolean online

) {}