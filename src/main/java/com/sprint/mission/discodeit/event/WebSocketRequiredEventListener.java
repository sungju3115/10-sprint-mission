package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!kafka")
public class WebSocketRequiredEventListener {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMessage(MessageCreatedEvent event) {
        String destination = "/sub/channels." + event.getChannelId() + ".messages";
        MessageDTO messageDTO = messageService.find(event.getMessageId());
        template.convertAndSend(destination, messageDTO);
    }
}
