package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Query("SELECT c FROM Channel c JOIN ReadStatus rs ON rs.channel = c WHERE rs.user.id = :userId")
    List<Channel> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT rs.channel FROM ReadStatus rs JOIN rs.channel WHERE rs.user.id = :userId")
    List<Channel> findAllWithReadStatusByUserId(@Param("userId")UUID userId);

    @Query("""
            SELECT DISTINCT c
            FROM Channel c
            LEFT JOIN ReadStatus rs ON rs.channel = c AND rs.user.id = :userId
            WHERE c.type = com.sprint.mission.discodeit.entity.ChannelType.PUBLIC
               OR rs.id IS NOT NULL
            """)
    List<Channel> findVisibleChannelsByUserId(@Param("userId") UUID userId);
}
