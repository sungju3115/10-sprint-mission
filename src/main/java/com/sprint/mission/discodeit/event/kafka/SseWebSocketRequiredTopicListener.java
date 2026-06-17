package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("kafka")
public class SseWebSocketRequiredTopicListener {

    private final SseService sseService;
    private final SimpMessagingTemplate template;
    private final MessageService messageService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.channels.created",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onChannelCreated(String payload) throws JsonProcessingException {
        ChannelDTO dto = objectMapper.readValue(payload, ChannelDTO.class);
        if (dto.type() == ChannelType.PRIVATE) {
            List<UUID> participantIds = dto.participants().stream().map(UserDTO::id).toList();
            sseService.send(participantIds, "channels.created", dto);
        } else {
            sseService.broadcast("channels.created", dto);
        }
        log.debug("SSE 전송 - channels.created: channelId={}", dto.id());
    }

    @KafkaListener(topics = "discodeit.channels.updated",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onChannelUpdated(String payload) throws JsonProcessingException {
        ChannelDTO dto = objectMapper.readValue(payload, ChannelDTO.class);
        sseService.broadcast("channels.updated", dto);
        log.debug("SSE 전송 - channels.updated: channelId={}", dto.id());
    }

    @KafkaListener(topics = "discodeit.channels.deleted",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onChannelDeleted(String payload) {
        UUID channelId = UUID.fromString(payload.replace("\"", ""));
        sseService.broadcast("channels.deleted", channelId);
        log.debug("SSE 전송 - channels.deleted: channelId={}", channelId);
    }

    @KafkaListener(topics = "discodeit.users.created",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onUserCreated(String payload) throws JsonProcessingException {
        UserDTO dto = objectMapper.readValue(payload, UserDTO.class);
        sseService.broadcast("users.created", dto);
        log.debug("SSE 전송 - users.created: userId={}", dto.id());
    }

    @KafkaListener(topics = "discodeit.users.updated",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onUserUpdated(String payload) throws JsonProcessingException {
        UserDTO dto = objectMapper.readValue(payload, UserDTO.class);
        sseService.broadcast("users.updated", dto);
        log.debug("SSE 전송 - users.updated: userId={}", dto.id());
    }

    @KafkaListener(topics = "discodeit.users.deleted",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onUserDeleted(String payload) {
        UUID userId = UUID.fromString(payload.replace("\"", ""));
        sseService.broadcast("users.deleted", userId);
        log.debug("SSE 전송 - users.deleted: userId={}", userId);
    }

    @KafkaListener(topics = "discodeit.notifications.created",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onNotificationCreated(String payload) throws JsonProcessingException {
        NotificationDto dto = objectMapper.readValue(payload, NotificationDto.class);
        sseService.send(List.of(dto.receiverId()), "notifications.created", dto);
        log.debug("SSE 전송 - notifications.created: receiverId={}", dto.receiverId());
    }

    @KafkaListener(topics = "discodeit.binaryContents.updated",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onBinaryContentUpdated(String payload) throws JsonProcessingException {
        BinaryContentDTO dto = objectMapper.readValue(payload, BinaryContentDTO.class);
        sseService.broadcast("binaryContents.updated", dto);
        log.debug("SSE 전송 - binaryContents.updated: contentId={}", dto.id());
    }

    @KafkaListener(topics = "discodeit.users.loginout",
            groupId = "${spring.application.name}-sse-${spring.application.instance-id}")
    public void onUserLogInOut(String payload) throws JsonProcessingException {
        UserLogInOutEvent event = objectMapper.readValue(payload, UserLogInOutEvent.class);
        UserDTO baseUser = userService.find(event.userId());
        UserDTO statusUser = new UserDTO(
                baseUser.id(), baseUser.username(), baseUser.email(),
                baseUser.profile(), event.online(), baseUser.role()
        );
        sseService.broadcast("users.updated", statusUser);
        log.debug("SSE 전송 - users.updated (loginout): userId={}, online={}", event.userId(), event.online());
    }

    @KafkaListener(topics = "discodeit.MessageCreatedEvent",
            groupId = "${spring.application.name}-ws-${spring.application.instance-id}")
    @Transactional
    public void onMessageCreated(String payload) throws JsonProcessingException {
        com.sprint.mission.discodeit.event.MessageCreatedEvent event =
                objectMapper.readValue(payload, com.sprint.mission.discodeit.event.MessageCreatedEvent.class);
        String destination = "/sub/channels." + event.getChannelId() + ".messages";
        MessageDTO messageDTO = messageService.find(event.getMessageId());
        template.convertAndSend(destination, messageDTO);
        log.debug("WebSocket 전송 - messages: channelId={}", event.getChannelId());
    }
}
