package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusCreateRequest request);
    UserStatusResponse findByUserId(UUID userId);
    List<UserStatusResponse> findAll();
    UserStatusResponse updateByUserID(UUID userId, UserStatusUpdateRequest request);
    void delete(UUID userStatusId);

}
