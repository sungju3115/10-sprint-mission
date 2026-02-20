package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // public Channel 생성 - POST /api/channels/public
    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelResponse postPublicChannel(@RequestBody ChannelCreateRequestPublic request){
        return channelService.createPublic(request);
    }

    // private Channel 생성 - POST /api/channels/private
    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelResponse postPrivateChannel(@RequestBody ChannelCreateRequestPrivate request){
        return channelService.createPrivate(request);
    }

    // Channel 단건 조회 - GET /api/channels/{channelId} (201 Created)
    @GetMapping("/{channelId}")
    public ChannelResponse getChannel(@PathVariable UUID channelId){
        return channelService.find(channelId);
    }

    // User가 참여 중인 Channel 목록 조회 - GET /api/channels?userID=userId
    @GetMapping
    public List<ChannelResponse> getAllChannels(@RequestParam UUID userId){
        return channelService.findAllByUserID(userId);
    }

    // 채널 수정 - PATCH /api/channels/{channelId} (200 OK)
    @PatchMapping("/{channelId}")
    public ChannelResponse updateChannel(@PathVariable UUID channelId,
                                         @RequestBody ChannelUpdateRequest request){
        return channelService.updateName(channelId, request);
    }

    // 채널 삭제 - DELETE /api/channels/{channelId}
    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable UUID channelId){
        channelService.deleteChannel(channelId);
    }
}
