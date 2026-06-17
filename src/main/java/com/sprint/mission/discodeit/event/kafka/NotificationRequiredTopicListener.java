package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.StorageFailedEvent;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("kafka")
public class NotificationRequiredTopicListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    @Transactional
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            notificationService.createMessageNotification(event);
            log.info("알림 생성 완료 - MessageCreatedEvent: messageId={}", event.getMessageId());
        } catch (JsonProcessingException e) {
            log.error("Kafka 역직렬화 실패 - MessageCreatedEvent", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    @Transactional
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            NotificationCreateRequest request = new NotificationCreateRequest(
                    event.getUserId(),
                    "권한이 변경되었습니다.",
                    event.getOldRole() + "->" + event.getNewRole()
            );
            notificationService.create(request);
            log.info("알림 생성 완료 - RoleUpdatedEvent: userId={}", event.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Kafka 역직렬화 실패 - RoleUpdatedEvent", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    @Transactional
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            StorageFailedEvent event = objectMapper.readValue(kafkaEvent, StorageFailedEvent.class);
            userRepository.findAllByRole(Role.ADMIN).forEach(user ->
                    notificationService.create(
                            new NotificationCreateRequest(user.getId(), event.getTitle(), event.getMessage())
                    )
            );
            log.info("알림 생성 완료 - S3UploadFailedEvent: title={}", event.getTitle());
        } catch (JsonProcessingException e) {
            log.error("Kafka 역직렬화 실패 - S3UploadFailedEvent", e);
            throw new RuntimeException(e);
        }
    }

    // 알림 생성 후 SSE 이벤트를 Kafka로 발행 → 모든 인스턴스의 SseWebSocketRequiredTopicListener가 수신
    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(NotificationCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event.getNotificationDto());
            kafkaTemplate.send("discodeit.notifications.created", payload);
            log.info("Kafka 발행 - discodeit.notifications.created: receiverId={}", event.getNotificationDto().receiverId());
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - NotificationCreatedEvent", e);
        }
    }
}
