package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.repository.JPAReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChannelMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PUBLIC")
    Channel toEntity(ChannelCreateRequestPublic req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PRIVATE")
    Channel toEntity(ChannelCreateRequestPrivate req);

    @Mapping(target = "participantIds", source = "participantsIds")
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    ChannelDTO toDTO(Channel channel, List<UUID> participantsIds, Instant lastMessageAt);
}
