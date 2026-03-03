package com.sprint.mission.discodeit.dto.auth.response;

import com.sprint.mission.discodeit.dto.user.response.UserResponse;

public record AuthServiceResponse(
        UserResponse user
) {
}
