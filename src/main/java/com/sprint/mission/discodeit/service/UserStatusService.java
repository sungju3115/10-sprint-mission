package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDTO create(UserStatusCreateRequest request);
    UserStatusDTO find(UUID userStatusId);
    List<UserStatusDTO> findAll();
    UserStatusDTO update(UUID userStatusId, UserStatusUpdateRequest request);
    UserStatusDTO updateByUserID(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID userStatusId);

}
