package com.sprint.mission.discodeit.mapper.auth;


import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public UserResponse toResponse(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getProfileImageId(),
                status.isOnline()
        );}
}
