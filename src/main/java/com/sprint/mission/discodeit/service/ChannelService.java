package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDTO createPublic(ChannelCreateRequestPublic channelCreateRequestPublic);
    ChannelDTO createPrivate(ChannelCreateRequestPrivate channelCreateRequestPrivate);
    ChannelDTO find(UUID channelID);
    List<ChannelDTO> findAllByUserID(UUID userID);
    ChannelDTO update(UUID channelID, ChannelUpdateRequest request);
    default void update() {}
    // channel 자체 삭제
    void deleteChannel(UUID channelID);
}
