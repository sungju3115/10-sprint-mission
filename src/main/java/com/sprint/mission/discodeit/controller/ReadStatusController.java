package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Reader;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/readStatuses")
@Tag(name = "ReadStatus", description = "ReadStatus API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // ReadStatus 정보 생성 - POST /api/readStatuses (201 Created)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "읽음 정보 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "읽음 정보 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadStatusResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "channel 혹은 user를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("Channel or user not found"))
            )
    })
    public ResponseEntity<ReadStatusResponse> postReadStatus(@RequestBody ReadStatusCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.create(request));
    }

    // ReadStatus 정보 수정 - PATCH /api/readStatuses/{readStatusId} (200 OK)
    @PatchMapping("/{readStatusId}")
    @Operation(summary = "읽음 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 정보 수정 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadStatusResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "읽음 상태 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("ReadStatus not found"))
            )
    })
    public ResponseEntity<ReadStatusResponse> updateReadStatus(
            @Parameter(
                    description = "수정할 readStatusId",
                    example = "123e4567-e89b-12d3-a456-426655440000",
                    required = true,
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ){
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    // 특정 사용자의 메시지 수신 정보 조회 - GET /api/readStatuses?userId=userId
    @GetMapping
    @Operation(summary = "특정 User의 읽음 상태 정보 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 모든 읽음 상태 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject("User not found"))
            )
    })
    public ResponseEntity<List<ReadStatusResponse>> getReadStatusByUserId(
            @Parameter(description = "조회할 userId", example = "123e4567-e89b-12d3-a456-426655440000", required = true)
            @RequestParam UUID userId){
        return ResponseEntity.ok(readStatusService.findAllByUserID(userId));
    }
}
