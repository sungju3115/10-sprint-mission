package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // CRUD
    UserDTO create(UserCreateRequest useReq, Optional<MultipartFile> profile);
    UserDTO find(UUID id);
    List<UserDTO> findAll();
    UserDTO update(UUID userID, UserUpdateRequest request, Optional<MultipartFile> profile);
    default void update() {}
    void deleteUser(UUID userID);
}
