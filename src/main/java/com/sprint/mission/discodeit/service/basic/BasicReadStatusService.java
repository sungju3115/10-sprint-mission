package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class BasicReadStatusService implements ReadStatusService {
    // 필드
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDTO create(ReadStatusCreateRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.userId()));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.channelId()));

        ReadStatus readStatus = new ReadStatus(user, channel);
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(savedReadStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDTO find(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.findById(readStatusID)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found: " + readStatusID));

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDTO> findAllByUserId(UUID userID){
        return readStatusRepository.findAllByUser_Id(userID).stream()
                .map(readStatusMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ReadStatusDTO update(UUID readStatusId, ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusId));

        readStatus.updateLastReadTime();
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusID){
        if(!readStatusRepository.existsById(readStatusID)){
            throw new NoSuchElementException("ReadStatus not found: " + readStatusID);
        }
        readStatusRepository.deleteById(readStatusID);
    }
}
