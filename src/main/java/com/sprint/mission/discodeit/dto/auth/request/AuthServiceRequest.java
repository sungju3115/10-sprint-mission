package com.sprint.mission.discodeit.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 정보")
public record AuthServiceRequest(
        @NotNull
        @Schema(description = "로그인할 username", example = "홍길동")
        String username,

        @NotNull
        @Schema(description = "로그인 비밀번호", example = "cnrwlqjq")
        String password
) {
}
