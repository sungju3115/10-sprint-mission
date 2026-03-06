package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JPAUserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT rs.user FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    List<User> findAllByChannelId(@Param("channelId") UUID channelId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
