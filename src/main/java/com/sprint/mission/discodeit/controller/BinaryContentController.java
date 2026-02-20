package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // binary-Content 단건 조회 - GET /api/binaryContents/{binaryContentId}
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> getBinaryContent(@PathVariable UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // binary-content 다건 조회 - GET /api/binaryContents?binaryContentIds=binaryContentId1?binaryContentId2
    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> getBinaryContents(@RequestParam List<UUID> binaryContentIds){
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

}
