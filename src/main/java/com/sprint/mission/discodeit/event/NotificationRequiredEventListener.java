package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    @TransactionalEventListener
    public void on(MessageCreatedEvent event){
        // readStatus = true 조회

        // user 마다 Notification 생성 및 저장 (title, content)

    }

    @TransactionalEventListener
    public void on(RoleUpdatedEvent event){
        // 권한 바뀐 사용자에게 Notification 생성 및 저장
        // title, content
    }
}
