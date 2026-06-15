package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

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
        ChannelDTO dto = event.getChannelDTO();
        if (dto.type() == ChannelType.PRIVATE) {
            List<UUID> participantIds = dto.participants().stream()
                .map(user -> user.id())
                .toList();
            sseService.send(participantIds, "channels.created", dto);
        } else {
            sseService.broadcast("channels.created", dto);
        }
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
