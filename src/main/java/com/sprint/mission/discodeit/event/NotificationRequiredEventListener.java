package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        notificationService.createMessageNotification(event);
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        NotificationCreateRequest request = new NotificationCreateRequest(
                event.getUserId(),
                "권한이 변경되었습니다.",
                event.getOldRole() + "->" + event.getNewRole()
        );
        notificationService.create(request);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(StorageFailedEvent event){

        userRepository.findAllByRole(Role.ADMIN).forEach(user ->
            notificationService.create(
                    new NotificationCreateRequest(user.getId(), event.getTitle(), event.getMessage())
            )
        );
    }
}
