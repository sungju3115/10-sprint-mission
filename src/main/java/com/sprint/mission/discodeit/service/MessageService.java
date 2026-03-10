package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    // CRUD
    MessageDTO create(MessageCreateRequest Request, Optional<List<MultipartFile>> attachments);
    MessageDTO find(UUID messageID);
    List<MessageDTO> findMessagesByUser(UUID userId);
    PageResponse<MessageDTO> findMessagesByChannel(UUID channelId, int pageNumber);
    MessageDTO update(UUID messageId, MessageUpdateRequest request);
    void deleteMessage(UUID messageID);

}
