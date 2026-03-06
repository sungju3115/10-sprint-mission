package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublic(ChannelCreateRequestPublic channelCreateRequestPublic);
    ChannelResponse createPrivate(ChannelCreateRequestPrivate channelCreateRequestPrivate);
    ChannelResponse find(UUID channelID);
    List<ChannelResponse> findAllByUserID(UUID userID);
    ChannelResponse update(UUID channelID, ChannelUpdateRequest request);
    default void update() {}
    // channel 자체 삭제
    void deleteChannel(UUID channelID);
}
