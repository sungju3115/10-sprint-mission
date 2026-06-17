package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("kafka")
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // ── 기존: 알림/역할/스토리지 ──────────────────────────────────

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        publish("discodeit.MessageCreatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        publish("discodeit.RoleUpdatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(StorageFailedEvent event) {
        publish("discodeit.S3UploadFailedEvent", event);
    }

    // ── 채널 SSE ─────────────────────────────────────────────────

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelCreatedEvent event) {
        publishValue("discodeit.channels.created", event.getChannelDTO());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelUpdatedEvent event) {
        publishValue("discodeit.channels.updated", event.getChannelDTO());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelDeletedEvent event) {
        publishValue("discodeit.channels.deleted", event.getChannelId());
    }

    // ── 사용자 SSE ────────────────────────────────────────────────

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserCreatedEvent event) {
        publishValue("discodeit.users.created", event.getUserDTO());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserUpdatedEvent event) {
        publishValue("discodeit.users.updated", event.getUserDTO());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserDeletedEvent event) {
        publishValue("discodeit.users.deleted", event.getUserId());
    }

    // ── 바이너리 콘텐츠 SSE ───────────────────────────────────────

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(BinaryContentStatusUpdatedEvent event) {
        publishValue("discodeit.binaryContents.updated", event.getBinaryContentDTO());
    }

    // ── 로그인/로그아웃 (트랜잭션 외부 이벤트) ────────────────────

    @Async("eventTaskExecutor")
    @EventListener
    public void on(UserLogInOutEvent event) {
        publish("discodeit.users.loginout", event);
    }

    // ── 공통 직렬화 ───────────────────────────────────────────────

    private void publish(String topic, Object payload) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(payload));
            log.debug("Kafka 발행 - topic: {}", topic);
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - topic: {}", topic, e);
        }
    }

    private void publishValue(String topic, Object value) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(value));
            log.debug("Kafka 발행 - topic: {}", topic);
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - topic: {}", topic, e);
        }
    }
}
