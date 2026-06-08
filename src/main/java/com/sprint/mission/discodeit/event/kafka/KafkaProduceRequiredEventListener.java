package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.StorageFailedEvent;
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

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
            log.info("Kafka 발행 - discodeit.MessageCreatedEvent: messageId={}", event.getMessageId());
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - MessageCreatedEvent", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
            log.info("Kafka 발행 - discodeit.RoleUpdatedEvent: userId={}", event.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - RoleUpdatedEvent", e);
        }
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(StorageFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
            log.info("Kafka 발행 - discodeit.S3UploadFailedEvent: title={}", event.getTitle());
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패 - StorageFailedEvent", e);
        }
    }
}
