package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

// 사용자 온라인 상태
@Setter(AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this.user = user;
        setUser(user);
        this.lastActiveAt = Instant.now();
    }

    public void setUser(User user) {
        this.user = user;
        user.setUserStatus(this);
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
    }

    // 5분 이내면 online
    public boolean isOnline(){
        return lastActiveAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
