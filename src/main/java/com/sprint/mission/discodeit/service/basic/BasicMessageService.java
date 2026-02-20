package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    // 필드
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request, Optional<List<MultipartFile>> attachments) {
        User sender = userRepository.find(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.authorId()));
        Channel channel = channelRepository.find(request.channelId());

        // Channel이 private일 경우 sender가 해당 channel의 member인지 check
        if (channel.getType().equals("Private") && (!channel.getMembersList().contains(sender))) {
            throw new IllegalArgumentException("User is not in this channel." + request.channelId());
        }

        // 첨부파일 처리
        List<UUID> attachmentIds = new ArrayList<>();
        attachments.ifPresent(files -> {
            for (MultipartFile file : files) {
                try {
                    BinaryContent attachment = new BinaryContent(
                            file.getOriginalFilename(),
                            file.getBytes(),
                            file.getContentType()
                    );
                    attachmentIds.add(binaryContentRepository.save(attachment).getId());
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 실패", e);
                }
            }
        });

        // messsage 생성
        Message message = new Message(request.content(), sender, channel, attachmentIds);

        // sender ,channel에 추가
        sender.addMessage(message);
        channel.addMessage(message);

        // [저장]
        userRepository.save(sender);
        channelRepository.save(channel);
        messageRepository.save(message);
        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContents(),
                message.getChannel().getId(),
                message.getSender().getId(),
                message.getAttachmentIDs()
        );
    }

    @Override
    public MessageResponse find(UUID messageID) {
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));
        return new MessageResponse(
                msg.getId(),
                msg.getCreatedAt(),
                msg.getUpdatedAt(),
                msg.getContents(),
                msg.getChannel().getId(),
                msg.getSender().getId(),
                msg.getAttachmentIDs()
        );
    }

    @Override
    public List<MessageResponse> findAllByUserID(UUID userID) {
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getSender().getId().equals(userID))
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getCreatedAt(),
                        msg.getUpdatedAt(),
                        msg.getContents(),
                        msg.getChannel().getId(),
                        msg.getSender().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public List<MessageResponse> findAllByChannelID(UUID channelID) {
        return messageRepository.findAll().stream()
                .filter(msg -> msg.getChannel().getId().equals(channelID))
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getCreatedAt(),
                        msg.getUpdatedAt(),
                        msg.getContents(),
                        msg.getChannel().getId(),
                        msg.getSender().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getCreatedAt(),
                        msg.getUpdatedAt(),
                        msg.getContents(),
                        msg.getChannel().getId(),
                        msg.getSender().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public MessageResponse update(UUID messageID, MessageUpdateRequest request) {
        // [저장]
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));

        // content update
        if (request.newContent() != null) {
            msg.updateContents(request.newContent());

        }

        // attachment 업데이트
        if (request.attachments() != null) {
            for (BinaryContentCreateRequest req : request.attachments()) {
                BinaryContent attachment = new BinaryContent(req.fileName(), req.content(), req.contentType());
                BinaryContent savedAttach = binaryContentRepository.save(attachment);
                msg.addAttachment(savedAttach.getId());
            }
        }

        UUID userID = msg.getSender().getId();
        UUID channelID = msg.getChannel().getId();

        // sender의 messageList에 반영
        User sender = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        for(Message m : sender.getMessageList()){
            if(m.getId().equals(msg.getId())){
                m.updateContents(request.newContent());
            }
        }
        userRepository.save(sender);

        // channel에서 반영
        Channel channel = channelRepository.find(channelID);
        for(Message m : channel.getMessageList()){
            if(m.getId().equals(messageID)){
                m.updateContents(request.newContent());
            }
        }
        channelRepository.save(channel);

        msg = messageRepository.save(msg);

        return new MessageResponse(
                msg.getId(),
                msg.getCreatedAt(),
                msg.getUpdatedAt(),
                msg.getContents(),
                msg.getChannel().getId(),
                msg.getSender().getId(),
                msg.getAttachmentIDs()
        );
    }

    @Override
    public void deleteMessage(UUID messageID) {
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));

        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        UUID senderID = sender.getId();
        UUID channelID = channel.getId();

        // 첨부 파일 삭제
        for (UUID attachmentID : msg.getAttachmentIDs()) {
            binaryContentRepository.delete(attachmentID);
        }

        // 여기 check
        sender = userRepository.find(senderID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderID));
        channel = channelRepository.find(channelID);

        // sender, channel에서 msg 삭제
        sender.removeMessage(msg);
        channel.removeMessage(msg);

        messageRepository.deleteMessage(msg.getId());
        userRepository.save(sender);
        channelRepository.save(channel);
    }

    @Override
    public List<MessageResponse> findMessagesByChannel(UUID channelID) {
        Channel channel = channelRepository.find(channelID);
        return channel.getMessageList().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getCreatedAt(),
                        msg.getUpdatedAt(),
                        msg.getContents(),
                        msg.getChannel().getId(),
                        msg.getSender().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public List<MessageResponse> findMessagesByUser(UUID userID) {
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        return user.getMessageList().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getCreatedAt(),
                        msg.getUpdatedAt(),
                        msg.getContents(),
                        msg.getChannel().getId(),
                        msg.getSender().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }
}
