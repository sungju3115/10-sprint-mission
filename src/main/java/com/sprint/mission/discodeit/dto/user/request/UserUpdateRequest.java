package com.sprint.mission.discodeit.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "사용자 정보 수정 요청")
public record UserUpdateRequest(

        @Schema(description = "수정할 새로운 사용자 이름(닉네임)", example = "새로운승주")
        String newUsername,

        @Schema(description = "수정할 새로운 이메일 주소", example = "new_seungju@example.com")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String newEmail,

        @Schema(description = "수정할 새로운 비밀번호", example = "newpassword123!")
        String newPassword
) { }