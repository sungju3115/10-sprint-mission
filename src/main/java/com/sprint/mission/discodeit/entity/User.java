package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {
    // Getter
    // 필드
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private UserStatus userStatus;

    // 생성자
    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.setUserStatus(new UserStatus());
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
        if (this.userStatus != null && this.userStatus.getUser() == null) {
            userStatus.setUser(this);
        }
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.username = name;
        updateUpdatedAt();
    }
    public void updateEmail(String email) {
        this.email = email;
    }
    public void updatePassword(String password) {
        this.password = password;
    }
    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }
}

