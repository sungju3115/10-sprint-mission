package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        sseService.send(
            List.of(event.getNotificationDto().receiverId()),
            "notifications.created",
            event.getNotificationDto()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentStatusUpdated(BinaryContentStatusUpdatedEvent event) {
        sseService.broadcast("binaryContents.updated", event.getBinaryContentDTO());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelCreated(ChannelCreatedEvent event) {
        sseService.broadcast("channels.created", event.getChannelDTO());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelUpdated(ChannelUpdatedEvent event) {
        sseService.broadcast("channels.updated", event.getChannelDTO());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelDeleted(ChannelDeletedEvent event) {
        sseService.broadcast("channels.deleted", event.getChannelId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        sseService.broadcast("users.created", event.getUserDTO());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserUpdatedEvent event) {
        sseService.broadcast("users.updated", event.getUserDTO());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        sseService.broadcast("users.deleted", event.getUserId());
    }
}
