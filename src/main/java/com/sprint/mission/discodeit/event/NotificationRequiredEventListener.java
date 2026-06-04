package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private final NotificationService notificationService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        notificationService.createMessageNotification(event);
    }

    // Requires_New를 서비스에 걸면 위험해 리스너 쪽에 애너테이션 추가
    // Q) 리스너에서 서비스로 notification 생성 로직을 아예 위임 vs Dto로 변환해서 create 사용 ?
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
}
