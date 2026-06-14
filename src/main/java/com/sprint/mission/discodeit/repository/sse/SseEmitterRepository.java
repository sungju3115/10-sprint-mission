package com.sprint.mission.discodeit.repository.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID userId, SseEmitter emitter) {
        data.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> findByUserId(UUID userId) {
        return data.getOrDefault(userId, List.of());
    }

    public Map<UUID, List<SseEmitter>> findAll() {
        return Collections.unmodifiableMap(data);
    }

    public void remove(UUID userId, SseEmitter emitter) {
        List<SseEmitter> emitters = data.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
