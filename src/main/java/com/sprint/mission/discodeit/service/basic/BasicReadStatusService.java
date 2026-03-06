package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.readStatus.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.repository.JPAUserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    // 필드
    private final JPAReadStatusRepository readStatusRepository;
    private final JPAUserRepository userRepository;
    private final JPAChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDTO create(ReadStatusCreateRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + request.channelId()));

        ReadStatus readStatus = readStatusMapper.toEntity(user, channel);
        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    public ReadStatusDTO find(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.findById(readStatusID)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusID));

        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    public List<ReadStatusDTO> findAllByUserId(UUID userID){
        return readStatusRepository.findAllByUserId(userID).stream()
                .map(readStatusMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ReadStatusDTO update(UUID readStatusId, ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusId));

        readStatus.updateLastReadTime();
        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusID){
        if(!readStatusRepository.existsById(readStatusID)){
            throw new IllegalArgumentException("ReadStatus not found: " + readStatusID);
        }
        readStatusRepository.deleteById(readStatusID);
    }
}
