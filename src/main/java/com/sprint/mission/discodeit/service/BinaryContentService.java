package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDTO create(BinaryContentCreateRequest request);
    BinaryContentDTO find(UUID contentID);
    List<BinaryContentDTO> findAllByIdIn(List<UUID> contentIDs);
    void delete(UUID contentID);
    Resource download(UUID contentID);
}
