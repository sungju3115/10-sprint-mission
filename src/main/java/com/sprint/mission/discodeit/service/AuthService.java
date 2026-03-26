package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;

public interface AuthService {
    UserDTO login(AuthServiceRequest authServiceRequest);
}
