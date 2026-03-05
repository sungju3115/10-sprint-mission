package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// 사용자가 채널 별로 가장 최근 메시지를 읽은 시간을 나타냄
@Entity
@Table(name = "read_statuses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column
    Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadTime() {
        lastReadAt = Instant.now();
        updateUpdatedAt();
    }
}
