package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDTO create(UserStatusCreateRequest request) {
        log.info("UserStatus 생성 요청 - userId: {}", request.userID());
        User user = userRepository.findById(request.userID())
                .orElseThrow(() -> {
                    log.warn("UserStatus 생성 실패 - 존재하지 않는 userId: {}", request.userID());
                    return new UserNotFoundException(request.userID());
                });

        // 같은 userId에 대한 UserStatus 중복 생성 방지
        if(user.getUserStatus() != null){
            log.debug("UserStatus 이미 존재 - userId: {}", request.userID());
            return userStatusMapper.toDTO(user.getUserStatus());
        }

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        log.info("UserStatus 생성 성공 - userId: {}", request.userID());
        return userStatusMapper.toDTO(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDTO find(UUID userStatusId){
        log.debug("UserStatus 단건 조회 요청 - userStatusId: {}", userStatusId);
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toDTO)
                .orElseThrow(() -> {
                    log.warn("UserStatus 조회 실패 - 존재하지 않는 userStatusId: {}", userStatusId);
                    return new UserStatusNotFoundException(userStatusId);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDTO> findAll(){
        log.debug("전체 UserStatus 조회 요청");
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public UserStatusDTO update(UUID userStatusId, UserStatusUpdateRequest request) {
        log.debug("UserStatus 업데이트 요청 - userStatusId: {}", userStatusId);
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> {
                    log.warn("UserStatus 업데이트 실패 - 존재하지 않는 userStatusId: {}", userStatusId);
                    return new UserStatusNotFoundException(userStatusId);
                });
        userStatus.updateLastActiveAt(newLastActiveAt);
        userStatusRepository.save(userStatus);
        log.debug("UserStatus 업데이트 성공 - userStatusId: {}", userStatusId);
        return userStatusMapper.toDTO(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDTO updateByUserID(UUID userId, UserStatusUpdateRequest request) {
        log.debug("UserStatus 업데이트 요청 - userId: {}", userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("UserStatus 업데이트 실패 - 존재하지 않는 userStatus, userId: {}", userId);
                    return UserStatusNotFoundException.byUserId(userId);
                });
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        userStatusRepository.save(userStatus);
        log.debug("UserStatus 업데이트 성공 - userId: {}", userId);
        return userStatusMapper.toDTO(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        log.debug("UserStatus 삭제 요청 - userStatusId: {}", userStatusId);
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> {
                    log.warn("UserStatus 삭제 실패 - 존재하지 않는 userStatusId: {}", userStatusId);
                    return new UserStatusNotFoundException(userStatusId);
                });
        userStatusRepository.deleteById(userStatus.getId());
        log.debug("UserStatus 삭제 성공 - userStatusId: {}", userStatusId);
    }
}
