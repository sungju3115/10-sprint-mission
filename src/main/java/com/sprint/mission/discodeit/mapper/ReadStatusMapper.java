package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    // Entity -> DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    ReadStatusDTO toDto(ReadStatus readStatus);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "channel.id", source = "channelId")
    @Mapping(target = "lastReadAt", expression = "java(java.time.Instant.now())")
    ReadStatus toEntity(ReadStatusCreateRequest request);
}
