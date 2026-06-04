package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    // 메타 데이터 커밋 이후 바이너리 데이터 저장 : AFTER_COMMIT
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    // Todo: DB 트랜잭션 점유를 이벤트 발행 메서드로 가져가는 것 보다는 Status Update만 점유하도록 하는 게 맞나 ?
    // 트랜잭션 A -> 커밋 -> 이벤트 발행 -> storage.put -> 새 트랜잭션 (updateStatus)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        try {
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
        } catch (Exception e) {
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
        }
    }
}
