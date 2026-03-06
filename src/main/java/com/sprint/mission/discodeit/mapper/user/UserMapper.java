package com.sprint.mission.discodeit.mapper.user;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {
    @Mapping(target = "profile", source = "user.profile")
    @Mapping(target = "online", expression = "java(user.getUserStatus() != null && user.getUserStatus().isOnline())")
    UserDTO toDTO(User user);

    User toEntity(UserCreateRequest request);
}
