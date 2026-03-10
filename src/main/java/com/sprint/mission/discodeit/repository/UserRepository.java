package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT rs.user FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    List<User> findAllByChannelId(@Param("channelId") UUID channelId);

    @Query("SELECT rs FROM ReadStatus rs JOIN FETCH rs.user JOIN FETCH rs.channel WHERE rs.channel.id IN :channelIds")
    List<ReadStatus> findAllByChannelIdIn(@Param("channelIds") List<UUID> channelIds);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userStatus")
    List<User> findAllWithStatus();

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
