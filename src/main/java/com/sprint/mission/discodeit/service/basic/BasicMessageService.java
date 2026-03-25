package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    // 필드
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final ReadStatusRepository readStatusRepository;
    private final PageResponseMapper pageResponseMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    @Transactional
    public MessageDTO create(MessageCreateRequest request, List<BinaryContentCreateRequest> requests) {
        // user, channel 존재 check
        User sender = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.authorId()));
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.channelId()));

        // Channel이 private일 경우 sender가 해당 channel의 member인지 check
        if (channel.getType() == ChannelType.PRIVATE && (!readStatusRepository.existsByUser_IdAndChannel_Id(sender.getId(), channel.getId()))) {
            throw new IllegalArgumentException("User is not in this channel." + request.channelId());
        }

        // 첨부파일 수정
        List<BinaryContent> attachments = requests.stream()
                .map(req -> {
                    String fileName = req.fileName();
                    String contentType = req.contentType();
                    byte[] bytes = req.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes.length);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                }).toList();

        // message 생성
        Message message = new Message(request.content(), channel, sender, attachments);
        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDTO(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDTO find(UUID messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        return messageMapper.toDTO(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> findMessagesByUser(UUID userId) {
        return messageRepository.findAllByAuthor_Id(userId).stream()
                .map(messageMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> findMessagesByChannel(UUID channelId, Instant createdAt, Pageable pageable) {
        Slice<MessageDTO> messageDTOSlice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(createdAt).orElse(Instant.now()), pageable)
                .map(messageMapper::toDTO);

        Instant nextCursor = null;
        if (!messageDTOSlice.getContent().isEmpty()) {
            nextCursor = messageDTOSlice.getContent().get(messageDTOSlice.getContent().size() - 1).createdAt();
        }

        return pageResponseMapper.fromSlice(messageDTOSlice, nextCursor)
;    }

    @Override
    @Transactional
    public MessageDTO update(UUID messageId, MessageUpdateRequest request) {
        // [저장]
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));

        if (request.newContent() != null) {
            msg.updateContents(request.newContent());
        }
        return messageMapper.toDTO(msg);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageID) {
        Message msg = messageRepository.findById(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));

        messageRepository.delete(msg);
    }

}
