package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    UserStatus toEntity(User user);

    @Mapping(target = "userId", source = "user.id")
    UserStatusDTO toDTO(UserStatus userStatus);
}
