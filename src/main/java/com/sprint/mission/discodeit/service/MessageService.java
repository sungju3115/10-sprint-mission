package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    // CRUD
    MessageResponse create(MessageCreateRequest Request, Optional<List<MultipartFile>> attachments);
    MessageResponse find(UUID messageID);
    List<MessageResponse> findMessagesByUser(UUID userId);
    List<MessageResponse> findMessagesByChannel(UUID channelId);
    MessageResponse update(UUID messageId, MessageUpdateRequest request);
    void deleteMessage(UUID messageID);

}
