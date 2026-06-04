package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDto create(NotificationCreateRequest request);
    List<NotificationDto> findAll(UUID receiverId);
    void delete(UUID notificationId, UUID receiverId);
    void createMessageNotification(MessageCreatedEvent event);
}
