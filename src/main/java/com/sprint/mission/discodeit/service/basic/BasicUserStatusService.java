package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusDTO;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
        // userId의 user 존재 여부 검증
        User user = userRepository.findById(request.userID())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.userID()));

        // 같은 userId에 대한 UserStatus 중복 생성 방지
        if(user.getUserStatus() != null){
            return userStatusMapper.toDTO(user.getUserStatus());
        }

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDTO(userStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatusDTO find(UUID userStatusId){
        return userStatusRepository.findById(userStatusId)
                .map(userStatusMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserStatusDTO> findAll(){
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public UserStatusDTO update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));
        userStatus.updateLastActiveAt(newLastActiveAt);
        return userStatusMapper.toDTO(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDTO updateByUserID(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userId));
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatusMapper.toDTO(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));
        userStatusRepository.deleteById(userStatus.getId());
    }
}
