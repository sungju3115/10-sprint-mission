package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {
    private final ChannelService channelService;

    // public Channel 생성 - POST /api/channels/public
    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(
            responseCode = "201",
            description = "Public channel 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema (implementation = ChannelResponse.class)
            )
    )
    public ChannelResponse postPublicChannel(@RequestBody ChannelCreateRequestPublic request){
        return channelService.createPublic(request);
    }

    // private Channel 생성 - POST /api/channels/private
    @PostMapping("/private")
    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(
            responseCode = "201",
            description = "Private channel 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema (implementation = ChannelResponse.class)
            )
    )
    public ChannelResponse postPrivateChannel(@RequestBody ChannelCreateRequestPrivate request){
        return channelService.createPrivate(request);
    }

    // Channel 단건 조회 - GET /api/channels/{channelId} (201 Created)
    @GetMapping("/{channelId}")
    @Operation(summary = "Channel 단건 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema (implementation = ChannelResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject("Channel not found")
                    )
            )
    })
    public ChannelResponse getChannel(
            @Parameter(
                    description = "조회할 채널 Id",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID channelId
    ){
        return channelService.find(channelId);
    }

    // User가 참여 중인 Channel 목록 조회 - GET /api/channels?userID=userId
    @GetMapping
    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponse(
            responseCode = "200",
            description = "user의 Channel 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class))
            ))
    public List<ChannelResponse> getAllChannels(
            @Parameter(
                    description = "조회할 userId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @RequestParam UUID userId){
        return channelService.findAllByUserID(userId);
    }

    // 채널 수정 - PATCH /api/channels/{channelId} (200 OK)
    @PatchMapping("/{channelId}")
    @Operation(summary = "channel 정보 수정", operationId = "update_3")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "채널 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema (implementation = ChannelResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "채널을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject("Channel not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private channel은 수정 불가함",
                    content = @Content(
                            examples = @ExampleObject("Private channel cannot be updated")
                    )
            )
    })
    public ChannelResponse updateChannel(
            @Parameter(
                    description = "수정할 Channel Id",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ){
        return channelService.updateName(channelId, request);
    }

    // 채널 삭제 - DELETE /api/channels/{channelId}
    @DeleteMapping("/{channelId}")
    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "channel 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Channel 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject("Channel not found")
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(
            @Parameter(
                    description = "삭제할 Channel Id",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID channelId){
        channelService.deleteChannel(channelId);
    }
}
