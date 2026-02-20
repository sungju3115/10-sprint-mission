package com.sprint.mission.discodeit.dto.auth.response;

import com.sprint.mission.discodeit.dto.user.response.UserCurrentStatusResponse;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;

import java.util.UUID;

public record AuthServiceResponse(
        UserResponse user
) {
}
