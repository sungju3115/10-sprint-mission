package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    // 필드
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request){
        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        ReadStatus newReadStatus = readStatusRepository.save(readStatus);
        return new ReadStatusResponse(
                newReadStatus.getId(),
                newReadStatus.getCreatedAt(),
                newReadStatus.getUpdatedAt(),
                newReadStatus.getUserID(),
                newReadStatus.getChannelID(),
                newReadStatus.getLastReadTime());
    }

    @Override
    public ReadStatusResponse find(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.find(readStatusID)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusID));

        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserID(),
                readStatus.getChannelID(),
                readStatus.getLastReadTime()
        );
    }

    @Override
    public List<ReadStatusResponse> findAllByUserID(UUID userID){
        return readStatusRepository.findByUserID(userID).stream()
                .map(rs -> new ReadStatusResponse(
                        rs.getId(),
                        rs.getCreatedAt(),
                        rs.getUpdatedAt(),
                        rs.getUserID(),
                        rs.getChannelID(),
                        rs.getLastReadTime()
                ))
                .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.find(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusId));

        readStatus.updateLastReadTime();
        ReadStatus newReadStatus = readStatusRepository.save(readStatus);

        return new ReadStatusResponse(
                newReadStatus.getId(),
                newReadStatus.getCreatedAt(),
                newReadStatus.getUpdatedAt(),
                newReadStatus.getUserID(),
                newReadStatus.getChannelID(),
                newReadStatus.getLastReadTime());
    }

    @Override
    public void delete(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.find(readStatusID)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusID));

        readStatusRepository.delete(readStatus.getId());
    }
}
