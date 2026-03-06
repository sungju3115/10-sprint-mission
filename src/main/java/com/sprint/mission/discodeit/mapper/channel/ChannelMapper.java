package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.repository.JPAReadStatusRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    JPAMessageRepository messageRepository;
    JPAReadStatusRepository readStatusRepository;
    UserMapper userMapper;
    // public 일때
    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPublic channelCreateRequestPublic){
        return new Channel(channelCreateRequestPublic.name(), channelCreateRequestPublic.description());
    }

    // private 일 때
    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPrivate channelCreateRequestPrivate){
        return Channel.createPrivateChannel();
    }

    // Entity -> DTO
    public ChannelResponse toResponse(Channel channel, List<UUID> participantsIds, Instant lastMessageAt){
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                participantsIds,
                lastMessageAt
        );}
}
