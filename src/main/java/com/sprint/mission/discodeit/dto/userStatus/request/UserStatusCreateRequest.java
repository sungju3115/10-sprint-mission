package com.sprint.mission.discodeit.dto.userStatus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "사용자 상태 정보 생성 요청")
public record UserStatusCreateRequest(
        @Schema(description = "상태를 생성할 사용자 ID", example = "123e4567-e89b-12d3-a456-426655440000")
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userID
) {
}