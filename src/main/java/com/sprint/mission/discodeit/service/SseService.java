package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {
    private static final long EMITTER_TIMEOUT = 1000L * 60 * 30;

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);

        sseEmitterRepository.save(receiverId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onError(e -> sseEmitterRepository.remove(receiverId, emitter));

        ping(emitter);

        if (lastEventId != null) {
            sseMessageRepository.findAfter(lastEventId).stream()
                .filter(msg -> msg.getReceiverIds() == null || msg.getReceiverIds().contains(receiverId))
                .forEach(msg -> sendToEmitter(emitter, msg));
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, eventName, data, receiverIds);
        sseMessageRepository.save(message);

        receiverIds.forEach(receiverId ->
            sseEmitterRepository.findByUserId(receiverId)
                .forEach(emitter -> sendToEmitter(emitter, message))
        );
    }

    public void broadcast(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, eventName, data, null);
        sseMessageRepository.save(message);

        sseEmitterRepository.findAll().values().stream()
            .flatMap(List::stream)
            .forEach(emitter -> sendToEmitter(emitter, message));
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        sseEmitterRepository.findAll().forEach((userId, emitters) ->
            emitters.removeIf(emitter -> !ping(emitter))
        );
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event().comment("ping"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendToEmitter(SseEmitter emitter, SseMessage message) {
        try {
            emitter.send(SseEmitter.event()
                .id(message.getId().toString())
                .name(message.getEventName())
                .data(message.getData()));
        } catch (IOException e) {
            log.debug("SSE 전송 실패 - eventName: {}", message.getEventName());
        }
    }
}
