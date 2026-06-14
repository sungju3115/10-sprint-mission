package com.sprint.mission.discodeit.repository.sse;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {
    private static final int MAX_SIZE = 1000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(SseMessage message) {
        messages.put(message.getId(), message);
        eventIdQueue.addLast(message.getId());
        if (eventIdQueue.size() > MAX_SIZE) {
            UUID oldest = eventIdQueue.pollFirst();
            messages.remove(oldest);
        }
    }

    public List<SseMessage> findAfter(UUID lastEventId) {
        boolean found = false;
        List<SseMessage> result = new ArrayList<>();
        for (UUID id : eventIdQueue) {
            if (found) {
                SseMessage msg = messages.get(id);
                if (msg != null) result.add(msg);
            } else if (id.equals(lastEventId)) {
                found = true;
            }
        }
        return result;
    }
}
