package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    // 필드
    private final JPAUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO create(UserCreateRequest userRequest, Optional<MultipartFile> profile) {
        // 이름, 이메일 유효성 검증
        validateName(userRequest.username());
        validateEmail(userRequest.email());

        // user 생성 with DTO
        User user = userMapper.toEntity(userRequest);
        UserStatus userStatus = new UserStatus(user);

        user.setUserStatus(userStatus);

        // 선택적으로 프로필 등록
        profile.ifPresent(file -> {
                 try{
                    BinaryContent bc = new BinaryContent(
                         file.getOriginalFilename(),
                         file.getContentType(),
                         file.getBytes()
                    );
                    user.updateProfile(bc);
                 } catch (IOException e){
                     throw new RuntimeException("파일 처리 실패" + e.getMessage());
                 }
                });

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO find(UUID userId) {
        // user 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    // 이름. 프로필 선택적 업데이트
    @Override
    @Transactional
    public UserDTO update(UUID userID, UserUpdateRequest request, Optional<MultipartFile> profile) {
        // user 조회
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // user 이름 선택적 업데이트
        Optional.ofNullable(request.newUsername()).ifPresent(name -> {
            validateName(name);
            user.updateName(name);
        });

        // user 이메일 선택적 업데이트
        Optional.ofNullable(request.newEmail()).ifPresent(email -> {
            validateEmail(email);
            user.updateEmail(email);
        });

        // user의 프로필 선택적 업데이트
        profile.ifPresent(file -> {
                    try{
                        BinaryContent bc = new BinaryContent(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        );
                        user.updateProfile(bc);
                    } catch (IOException e){
                        throw new RuntimeException("파일 처리 실패" + e.getMessage());
                    }
                });

        return userMapper.toDTO(user);
    }

    // user가 해당 ch에서 보낸 msg 삭제 반영 X
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        // 존재하는 user인지 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // [저장]
        userRepository.deleteById(user.getId());
    }

    // User 이름 유효성 검증
    public void validateName(String username){
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("Already Present name: " + username);
        }
    }

    // 이메일 유효성 검증
    public void validateEmail(String email){
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Already Present email: " + email);
        }
    }
}
