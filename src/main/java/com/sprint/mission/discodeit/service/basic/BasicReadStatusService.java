package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("ReadStatus 생성 요청 - userId: {}, channelId: {}", request.userId(), request.channelId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.warn("ReadStatus 생성 실패 - 존재하지 않는 userId: {}", request.userId());
                    return new UserNotFoundException(request.userId());
                });

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> {
                    log.warn("ReadStatus 생성 실패 - 존재하지 않는 channelId: {}", request.channelId());
                    return new ChannelNotFoundException(request.channelId());
                });

        ReadStatus readStatus = new ReadStatus(user, channel);
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
        log.info("ReadStatus 생성 성공 - readStatusId: {}", savedReadStatus.getId());
        return readStatusMapper.toDto(savedReadStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDTO find(UUID readStatusID){
        log.debug("ReadStatus 단건 조회 요청 - readStatusId: {}", readStatusID);
        ReadStatus readStatus = readStatusRepository.findById(readStatusID)
                .orElseThrow(() -> {
                    log.warn("ReadStatus 조회 실패 - 존재하지 않는 readStatusId: {}", readStatusID);
                    return new ReadStatusNotFoundException(readStatusID);
                });

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDTO> findAllByUserId(UUID userID){
        log.debug("사용자별 ReadStatus 조회 요청 - userId: {}", userID);
        return readStatusRepository.findAllByUser_Id(userID).stream()
                .map(readStatusMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ReadStatusDTO update(UUID readStatusId, ReadStatusUpdateRequest request){
        log.debug("ReadStatus 업데이트 요청 - readStatusId: {}", readStatusId);
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> {
                    log.warn("ReadStatus 업데이트 실패 - 존재하지 않는 readStatusId: {}", readStatusId);
                    return new ReadStatusNotFoundException(readStatusId);
                });

        readStatus.updateLastReadTime();
        readStatusRepository.save(readStatus);
        log.debug("ReadStatus 업데이트 성공 - readStatusId: {}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusID){
        log.debug("ReadStatus 삭제 요청 - readStatusId: {}", readStatusID);
        if(!readStatusRepository.existsById(readStatusID)){
            log.warn("ReadStatus 삭제 실패 - 존재하지 않는 readStatusId: {}", readStatusID);
            throw new ReadStatusNotFoundException(readStatusID);
        }
        log.debug("ReadStatus 삭제 성공 - readStatusId:");
        readStatusRepository.deleteById(readStatusID);
    }
}
