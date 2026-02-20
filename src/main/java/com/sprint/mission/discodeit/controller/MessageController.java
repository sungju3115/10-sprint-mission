package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 메시지 생성 - POST /api/messages (201 Created)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse postMessage(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                       @RequestPart(value="attachments", required = false) List<MultipartFile> attachments
                                       ){
        return messageService.create(request, Optional.ofNullable(attachments));
    }

    // 메시지 수정 - PATCH /api/messages/{messageId} (200 OK)
    @PatchMapping("/{messageId}")
    public MessageResponse updateMessage(@PathVariable UUID messageId,
                                         @RequestBody MessageUpdateRequest request){
        return messageService.update(messageId, request);
    }

    // 메시지 삭제 - DELETE /api/messages/{messageId} (204 No Content)
    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.deleteMessage(messageId);
    }

    // 특정 Channel 메시지 목록 조회 - GET /api/messages?channelId=channelId (200 OK)
    @GetMapping
    public List<MessageResponse> getAllMessages(@RequestParam UUID channelId){
        return messageService.findMessagesByChannel(channelId);
    }
}
