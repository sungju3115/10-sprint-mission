package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity{
    // 필드
    @Id
    @Column(columnDefinition="UUID")
    private final UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private final Instant createdAt;

    // 생성자
    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity baseEntity = (BaseEntity) o;
        return Objects.equals(id, baseEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
