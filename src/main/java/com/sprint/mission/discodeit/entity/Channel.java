package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;


@Entity
@Table(name = "channels")
@Getter
public class Channel extends BaseUpdatableEntity {
    // 필드
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    ChannelType type;

    @Column(length = 100)
    String name;

    @Column(length = 500)
    String description;

    // Public Channel 생성자
    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        type = ChannelType.PUBLIC;
    }

    // Private Channel 생성자
    public Channel(){
        super();
        type = ChannelType.PRIVATE;
    }

    // setter
    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt();
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
