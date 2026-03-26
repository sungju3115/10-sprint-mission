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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("메시지 생성 요청 - channelId: {}, authorId: {}", request.channelId(), request.authorId());
        // user, channel 존재 check
        User sender = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 authorId: {}", request.authorId());
                    return new NoSuchElementException("User not found: " + request.authorId());
                });
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> {
                    log.warn("메시지 생성 실패 - 존재하지 않는 channelId: {}", request.channelId());
                    return new NoSuchElementException("Channel not found: " + request.channelId());
                });

        // Channel이 private일 경우 sender가 해당 channel의 member인지 check
        if (channel.getType() == ChannelType.PRIVATE && (!readStatusRepository.existsByUser_IdAndChannel_Id(sender.getId(), channel.getId()))) {
            log.warn("메시지 생성 실패 - Private 채널 비멤버 접근: userId={}, channelId={}", sender.getId(), channel.getId());
            throw new IllegalArgumentException("User is not in this channel." + request.channelId());
        }

        // 첨부파일 수정
        List<BinaryContent> attachments = requests.stream()
                .map(req -> {
                    log.debug("첨부파일 저장 - fileName: {}, contentType: {}", req.fileName(), req.contentType());
                    String fileName = req.fileName();
                    String contentType = req.contentType();
                    byte[] bytes = req.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes.length);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                }).toList();
        log.debug("첨부파일 저장 완료 - 총 {}개", attachments.size());

        // message 생성
        Message message = new Message(request.content(), channel, sender, attachments);
        Message savedMessage = messageRepository.save(message);
        log.info("메시지 생성 성공 - messageId: {}", savedMessage.getId());
        return messageMapper.toDTO(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDTO find(UUID messageId) {
        log.info("메시지 단건 조회 요청 - messageId: {}", messageId);
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("메시지 조회 실패 - 존재하지 않는 messageId: {}", messageId);
                    return new IllegalArgumentException("Message not found: " + messageId);
                });
        return messageMapper.toDTO(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> findMessagesByUser(UUID userId) {
        log.debug("사용자별 메시지 조회 요청 - userId: {}", userId);
        return messageRepository.findAllByAuthor_Id(userId).stream()
                .map(messageMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> findMessagesByChannel(UUID channelId, Instant createdAt, Pageable pageable) {
        log.debug("채널별 메시지 조회 요청 - channelId: {}, cursor: {}", channelId, createdAt);
        Slice<MessageDTO> messageDTOSlice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(createdAt).orElse(Instant.now()), pageable)
                .map(messageMapper::toDTO);

        Instant nextCursor = null;
        if (!messageDTOSlice.getContent().isEmpty()) {
            nextCursor = messageDTOSlice.getContent().get(messageDTOSlice.getContent().size() - 1).createdAt();
            log.debug("nextCursor: {}", nextCursor);
        }

        return pageResponseMapper.fromSlice(messageDTOSlice, nextCursor)
;    }

    @Override
    @Transactional
    public MessageDTO update(UUID messageId, MessageUpdateRequest request) {
        log.info("메시지 수정 요청 - messageId: {}", messageId);
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("메시지 수정 실패 - 존재하지 않는 messageId: {}", messageId);
                    return new NoSuchElementException("Message not found: " + messageId);
                });

        if (request.newContent() != null) {
            log.debug("메시지 content 수정 - content: {}", request.newContent());
            msg.updateContents(request.newContent());
        }
        log.info("메시지 수정 성공 - messageId: {}", messageId);
        return messageMapper.toDTO(msg);
    }

    @Override
    @Transactional
    public void deleteMessage(UUID messageID) {
        log.info("메시지 삭제 요청 - messageId: {}", messageID);
        Message msg = messageRepository.findById(messageID)
                .orElseThrow(() -> {
                    log.warn("메시지 삭제 실패 - 존재하지 않는 messageId: {}", messageID);
                    return new IllegalArgumentException("Message not found: " + messageID);
                });

        messageRepository.delete(msg);
        log.info("메시지 삭제 성공 - messageId: {}", messageID);
    }

}
