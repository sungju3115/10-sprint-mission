package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD
    UserResponse create(UserCreateRequest useReq, Optional<MultipartFile> profile);
    UserResponse find(UUID id);
    List<UserResponse> findAll();
    UserResponse update(UUID userID, UserUpdateRequest request, Optional<MultipartFile> profile);
    default void update() {}
    void deleteUser(UUID userID);

    List<Channel> findJoinedChannels(UUID userID);
    void validateName(String name);
    void validateEmail(String email);

}
