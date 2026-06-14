package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final ReadStatusRepository readStatusRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @CacheEvict(value = "notifications", key = "#request.receiverId()")
    @Transactional
    public NotificationDto create(NotificationCreateRequest request) {
        User user = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new UserNotFoundException(request.receiverId()));

        Notification notification = new Notification(user, request.title(), request.content());

        notificationRepository.save(notification);
        NotificationDto result = notificationMapper.toDto(notification);
        applicationEventPublisher.publishEvent(new NotificationCreatedEvent(result));
        return result;
    }

    @Override
    @Cacheable(value = "notifications", key = "#receiverId")
    public List<NotificationDto> findAll(UUID receiverId) {
        return notificationRepository.findAllByReceiverId(receiverId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @CacheEvict(value = "notifications", key = "#receiverId")
    @Transactional
    public void delete(UUID notificationId, UUID receiverId) {
        Notification notification = notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        if (!notification.getReceiver().getId().equals(receiverId)) {
            // 403 - Access Denied
            throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.deleteById(notificationId);
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    @Transactional
    public void createMessageNotification(MessageCreatedEvent event) {
        // readStatus = true 조회
        // Todo: User가 많아지면 메모리 문제 발생 -> Batch 혹은 페이징
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId())
                .stream()
                .filter(rs -> !rs.getUser().getId().equals(event.getSenderId()))
                .toList();

        // user 마다 Notification 생성 및 저장 (title, content)
        List<Notification> notifications = readStatuses.stream()
                .map(rs -> new Notification(
                        rs.getUser(),
                        event.getSenderName() + " (#" + event.getChannelName() + ")",
                        event.getContent()
                ))
                .toList();

        notificationRepository.saveAll(notifications);
        notifications.forEach(n ->
            applicationEventPublisher.publishEvent(new NotificationCreatedEvent(notificationMapper.toDto(n)))
        );
    }
}
