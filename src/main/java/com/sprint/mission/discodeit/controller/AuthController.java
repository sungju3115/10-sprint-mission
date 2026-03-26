package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name="Auth", description = "인증 API")
public class AuthController {
    private final BasicAuthService authService;

    // 로그인 - POST /api/auth/login
    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("User with username : {username} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호가 일치하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("Wrong password")
                    )
            )
    })
    ResponseEntity<UserDTO> login(@RequestBody AuthServiceRequest request){
        log.debug("로그인 시도: username={}", request.username());
        return ResponseEntity.ok(authService.login(request));
    }
}
