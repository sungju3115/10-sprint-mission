package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        UserStatus userStatus = new UserStatus(request.userID());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);
        return new UserStatusResponse(
                savedUserStatus.getId(),
                savedUserStatus.getCreatedAt(),
                savedUserStatus.getUpdatedAt(),
                savedUserStatus.getUserID(),
                savedUserStatus.getLastActiveAt(),
                savedUserStatus.isOnline());
    }

    @Override
    public UserStatusResponse find(UUID userId){
        UserStatus userStatus = userStatusRepository.findByUserID(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userId));
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserID(),
                userStatus.getLastActiveAt(),
                userStatus.isOnline()
        );
    }

    @Override
    public List<UserStatusResponse> findAll(){
        return userStatusRepository.findAll().stream()
                .map(us -> new UserStatusResponse(
                        us.getId(),
                        us.getCreatedAt(),
                        us.getUpdatedAt(),
                        us.getUserID(),
                        us.getLastActiveAt(),
                        us.isOnline()
                )).toList();
    }

//    @Override
//    public UserStatusResponse update(UserStatusUpdateRequest request) {
//        UserStatus userStatus = userStatusRepository.find(request.userID())
//                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + request.userID()));
//        userStatus.updateLastLogin();
//        UserStatus newUserStatus = userStatusRepository.save(userStatus);
//        return new UserStatusResponse(newUserStatus.getUserID(), newUserStatus.isOnline());
//    }

    @Override
    public UserStatusResponse updateByUserID(UUID userID, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserID(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        userStatus.updateLastActiveAt(request.newLastActiveAt());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);
        return new UserStatusResponse(
                savedUserStatus.getId(),
                savedUserStatus.getCreatedAt(),
                savedUserStatus.getUpdatedAt(),
                savedUserStatus.getUserID(),
                savedUserStatus.getLastActiveAt(),
                savedUserStatus.isOnline());
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.find(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + userStatusId));
        userStatusRepository.deleteUserStatus(userStatus.getId());
    }
}
