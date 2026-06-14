package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

// 단순 텍스트 메시지일 떄
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    // pub/messages
    @MessageMapping("/messages")
    public void sendMessage(MessageCreateRequest messageCreateRequest){
        // Q) 빈 리스트로 보내기 vs 따로 메서드 빼기
        messageService.create(messageCreateRequest, List.of());
    }
}
