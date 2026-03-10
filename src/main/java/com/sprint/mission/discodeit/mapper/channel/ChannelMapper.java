package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.repository.JPAReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChannelMapper {
    @Autowired
    protected JPAReadStatusRepository readStatusRepository;

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PUBLIC")
    public abstract Channel toEntity(ChannelCreateRequestPublic req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "PRIVATE")
    public abstract Channel toEntity(ChannelCreateRequestPrivate req);

    @Mapping(target = "id", source = "channel.id")
    @Mapping(target = "type", source = "channel.type")
    @Mapping(target = "name", source = "channel.name")
    @Mapping(target = "description", source = "channel.description")
    // 파라미터로 받은 participants와 lastMessageAt을 직접 매핑
    @Mapping(target = "participants", expression = "java(toUserDTO(participantIds))")
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    public abstract ChannelDTO toDTO(Channel channel, List<UUID> participantIds, Instant lastMessageAt);

    protected List<UserDTO> toUserDTO(List<UUID> participantIds){
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> participantIds.contains(readStatus.getUser().getId()))
                .map(readStatus -> userMapper.toDTO(readStatus.getUser()))
                .toList();
    }
}
