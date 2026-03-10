package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannel_Id(UUID channelId);
    Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);
    Optional<Message> findByAuthorId(UUID authorId);
    List<Message> findAllByAuthor_Id(UUID authorId);

    @Query("SELECT Max(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
    Instant findFirstByChannelIdOrderByCreatedAtDesc(@Param("channelId") UUID channelId);
}
