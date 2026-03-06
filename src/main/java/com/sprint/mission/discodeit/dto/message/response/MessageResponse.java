package com.sprint.mission.discodeit.dto.message.response;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "메시지 상세 응답")
public record MessageResponse(
        @Schema(description = "메시지 고유 식별자(ID)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "메시지 생성 일시", example = "2026-02-24T07:50:33Z")
        Instant createdAt,

        @Schema(description = "메시지 최근 수정 일시", example = "2026-02-24T08:00:00Z")
        Instant updatedAt,

        @Schema(description = "메시지 내용", example = "반갑습니다! Discodeit 테스트 메시지입니다.")
        String content,

        @Schema(description = "해당 메시지가 소속된 채널 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        UUID channelId,

        @Schema(description = "메시지 작성자")
        UserResponse author,

        @Schema(description = "첨부된 파일(BinaryContent) ID 목록")
        List<BinaryContentResponse> attachmentIds
) {
}