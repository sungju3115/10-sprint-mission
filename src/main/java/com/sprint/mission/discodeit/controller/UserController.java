package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserStatusService userStatusService) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }

    // user 등록 - POST /api/users
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse postUser(@RequestPart UserCreateRequest request,
                                 @RequestPart(value="profile", required = false) MultipartFile profile){
        return userService.create(request, Optional.ofNullable(profile));
    }

    // user 정보 수정 - PATCH /api/users/{userId}
    @PatchMapping(value="/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse updateUser(@PathVariable UUID userId,
                                   @RequestPart("userUpdateRequest") UserUpdateRequest request,
                                   @RequestPart(value="profile", required = false) MultipartFile profile){
        return userService.update(userId, request, Optional.ofNullable(profile));
    }

    // user 삭제 - DELETE /api/users/{userId}
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId){
        userService.deleteUser(userId);
    }

    // user 단건 조회
    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId){
        return userService.find(userId);
    }

    // user 다건 조회 - GET /api/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // user 온라인 상태 업데이트 - PATCH /api/users/{userId}/userStatus
    @PatchMapping( "/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateStatus(@PathVariable UUID userId){
        return ResponseEntity.ok(userStatusService.updateByUserID(userId));
    }

}
