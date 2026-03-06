package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.userStatus.UserStatusMapper;
import com.sprint.mission.discodeit.repository.JPAUserRepository;
import com.sprint.mission.discodeit.repository.JPAUserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final JPAUserStatusRepository userStatusRepository;
    private final JPAUserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusResponse create(UserStatusCreateRequest request) {
        // userId의 user 존재 여부 검증
        User user = userRepository.findById(request.userID())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userID()));

        // 같은 userId에 대한 UserStatus 중복 생성 방지
        if(user.getUserStatus() != null){
            return userStatusMapper.toResponse(user.getUserStatus());
        }

        UserStatus userStatus = new UserStatus(user);
        user.setUserStatus(userStatus);

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    public UserStatusResponse findByUserId(UUID userId){
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userId));
        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll(){
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusResponse updateByUserID(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userId));
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userStatusId));
        userStatusRepository.deleteById(userStatus.getId());
    }
}
