package com.sprint.mission.discodeit.mapper.readStatus;

import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {
    // Entity -> DTO
    public ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt());
    }

    // DTO -> Entity
    public ReadStatus toEntity(User user, Channel channel){
        return new ReadStatus(user, channel);
    }
}
