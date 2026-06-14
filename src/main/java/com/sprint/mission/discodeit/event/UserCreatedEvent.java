package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreatedEvent {
    private final UserDTO userDTO;
}
