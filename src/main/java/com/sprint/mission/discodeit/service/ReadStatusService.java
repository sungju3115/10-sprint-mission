package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDTO create(ReadStatusCreateRequest request);
    ReadStatusDTO find(UUID readStatusID);
    List<ReadStatusDTO> findAllByUserId(UUID userID);
    ReadStatusDTO update(UUID readStatusId, ReadStatusUpdateRequest request);
    void delete(UUID readStatusID);
}
