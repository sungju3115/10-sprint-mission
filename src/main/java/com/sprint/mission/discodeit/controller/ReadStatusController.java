package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/api/readStatuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    // ReadStatus 정보 생성 - POST /api/readStatuses (201 Created)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReadStatusResponse> postReadStatus(@RequestBody ReadStatusCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(readStatusService.create(request));
    }

    // ReadStatus 정보 수정 - PATCH /api/readStatuses/{readStatusId} (200 OK)
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@PathVariable UUID readStatusId,
                                               @RequestBody ReadStatusUpdateRequest request){
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    // 특정 사용자의 메시지 수신 정보 조회 - GET /api/readStatuses?userId=userId
    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> getReadStatusByUserId(
            @RequestParam UUID userId){
        return ResponseEntity.ok(readStatusService.findAllByUserID(userId));
    }
}
