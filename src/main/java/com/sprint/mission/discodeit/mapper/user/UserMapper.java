package com.sprint.mission.discodeit.mapper.user;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    BinaryContentMapper binaryContentMapper;
    // Entity -> DTO
    public UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                binaryContentMapper.toDTO(user.getProfile()),
                userStatus.isOnline()
        );
    }

    // DTO -> Entity
    public User toEntity(UserCreateRequest request){
        return new User(
                request.username(),
                request.email(),
                request.password()
        );
    }
}
