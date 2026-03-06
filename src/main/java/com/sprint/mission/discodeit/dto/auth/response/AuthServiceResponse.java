package com.sprint.mission.discodeit.dto.auth.response;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;

public record AuthServiceResponse(
        UserDTO user
) {
}
