package com.sprint.mission.discodeit.mapper.channel;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    // public 일때

    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPublic channelCreateRequestPublic){
        return new Channel(channelCreateRequestPublic.name(), channelCreateRequestPublic.descriptions());
    }

    // Entity -> DTO
    public ChannelResponse toResponse(Channel channel){
        List<UUID> participantsIds = channel.getMembersList().stream().map(user -> user.getId()).toList();
        Instant lastMessageAt = channel.getMessageList().stream().map(message -> message.getCreatedAt()).max(Instant::compareTo).orElse(null);
        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantsIds,
                lastMessageAt
        );}

    // private 일 때
    // DTO -> Entity
    public Channel toEntity(ChannelCreateRequestPrivate channelCreateRequestPrivate){
        return new Channel();
    }
}
