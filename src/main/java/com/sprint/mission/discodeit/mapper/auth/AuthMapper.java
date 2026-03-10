package com.sprint.mission.discodeit.mapper.auth;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface AuthMapper {
    @Mapping(target = "profile", source = "user.profile")
    @Mapping(target = "online", expression = "java(user.getUserStatus().isOnline())")
    UserDTO toResponse(User user);
}
