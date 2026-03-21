package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


@MappedSuperclass
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate
    private Instant updatedAt;

    public void updateUpdatedAt() {
        updatedAt = Instant.now();
    }
}
