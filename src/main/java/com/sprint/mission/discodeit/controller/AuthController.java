package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.auth.response.AuthServiceResponse;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 로그인 - POST /api/auth/login
    @PostMapping("/login")
    ResponseEntity<UserResponse> login(@RequestBody AuthServiceRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
