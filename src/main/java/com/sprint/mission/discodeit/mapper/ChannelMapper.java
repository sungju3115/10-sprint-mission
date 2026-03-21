package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChannelMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PUBLIC")
    public abstract Channel toEntity(ChannelCreateRequestPublic req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PRIVATE")
    public abstract Channel toEntity(ChannelCreateRequestPrivate req);

}
