package com.sprint.mission.discodeit.mapper.userStatus;

import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {
    // DTO -> Entity
    public UserStatus toEntity(User user) {
        return new UserStatus(user);
    }

    // Entity -> DTO
    public UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(userStatus.getId(), userStatus.getUser().getId(), userStatus.getLastActiveAt());
    }
}
