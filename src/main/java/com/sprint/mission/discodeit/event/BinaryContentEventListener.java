package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        try {
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
        } catch (Exception e) {
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
        }
    }
}
