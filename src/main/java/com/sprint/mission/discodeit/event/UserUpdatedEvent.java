package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserUpdatedEvent {
    private final UserDTO userDTO;
}
