package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {
    private final BinaryContentStorage binaryContentStorage;

    // 메타 데이터 커밋 이후 바이너리 데이터 저장 : AFTER_COMMIT
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
    }
}
