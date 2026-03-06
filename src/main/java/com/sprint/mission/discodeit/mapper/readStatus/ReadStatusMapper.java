package com.sprint.mission.discodeit.mapper.readStatus;

import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    // Entity -> DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    ReadStatusDTO toResponse(ReadStatus readStatus);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "lastReadAt", expression = "java(java.time.Instant.now())")
    ReadStatus toEntity(User user, Channel channel);
}
