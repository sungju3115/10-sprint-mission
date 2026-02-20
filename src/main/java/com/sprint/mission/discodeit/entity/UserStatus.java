package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 사용자 별 마지막 접속 시간
@Getter
public class UserStatus extends Base{
    private final UUID userID;
    private Instant lastActiveAt;

    public UserStatus(UUID userID) {
        super();
        this.userID = userID;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
    }

    // 5분 이내면 online
    public boolean isOnline(){
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

}

