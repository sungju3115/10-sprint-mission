package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {
    private final NotificationDto notificationDto;
}
