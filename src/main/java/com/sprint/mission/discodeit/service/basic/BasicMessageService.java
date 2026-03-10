package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.message.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    // 필드
    private final JPAMessageRepository messageRepository;
    private final JPAUserRepository userRepository;
    private final JPAChannelRepository channelRepository;
    private final JPABinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final JPAReadStatusRepository readStatusRepository;

    @Override
    @Transactional
    public MessageDTO create(MessageCreateRequest request, Optional<List<MultipartFile>> attachments) {
        // user, channel 존재 check
        User sender = userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.authorId()));
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + request.channelId()));

        // Channel이 private일 경우 sender가 해당 channel의 member인지 check
        if (channel.getType() == ChannelType.PRIVATE && (readStatusRepository.existsByUser_IdAndChannel_Id(sender.getId(), channel.getId()))) {
            throw new IllegalArgumentException("User is not in this channel." + request.channelId());
        }

        // message 생성
        Message message = new Message(request.content(), channel, sender);

        // 첨부파일 처리, 로직 수정
        attachments.ifPresent(files -> {
            for (MultipartFile file : files) {
                try {
                    BinaryContent attachment = new BinaryContent(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getSize()
                    );

                    binaryContentStorage.put(attachment.getId(), file.getBytes());
                    message.getAttachments().add(attachment);
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 실패", e);
                }
            }
        });

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDTO(savedMessage);
    }

    @Override
    public MessageDTO find(UUID messageId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        return messageMapper.toDTO(msg);
    }

    @Override
    public List<MessageDTO> findMessagesByUser(UUID userId) {
        return messageRepository.findAllByAuthor_Id(userId).stream()
                .map(messageMapper::toDTO).toList();
    }

    @Override
    public List<MessageDTO> findMessagesByChannel(UUID channelID) {
        return messageRepository.findAllByChannel_Id(channelID).stream()
                .map(messageMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public MessageDTO update(UUID messageId, MessageUpdateRequest request) {
        // [저장]
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

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
