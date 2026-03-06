package com.sprint.mission.discodeit.mapper.message;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.binaryContent.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {
    private UserMapper userMapper;
    private BinaryContentMapper binaryContentMapper;
    // Entity -> DTO
    public MessageResponse toResponse(Message message) {
        List<BinaryContentResponse> binaryContentResponses = message.getAttachments().stream().map(binaryContentMapper::toDTO).toList();
        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                userMapper.toResponse(message.getAuthor(), message.getAuthor().getUserStatus()),
                binaryContentResponses
                );
    }

    // DTO -> Entity
    public Message toEntity(MessageCreateRequest request, Channel channel, User author){
        return new Message(request.content(), channel, author);
    }
}
