package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsEmailException;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {
    // 필드
    private final UserRepository userRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    @Transactional
    public UserDTO create(UserCreateRequest userRequest, Optional<MultipartFile> profile) {
        // 이름, 이메일 유효성 검증
        validateName(userRequest.username());
        validateEmail(userRequest.email());

        // user 생성 with DTO
        User user = new User(userRequest.username(), userRequest.email(), userRequest.password(), null);

        // 선택적으로 프로필 등록
        profile.ifPresent(file -> {
                 try{
                    log.debug("프로필 이미지 저장 - fileName: {}", file.getOriginalFilename());
                    BinaryContent bc = new BinaryContent(
                         file.getOriginalFilename(),
                         file.getContentType(),
                         file.getSize()
                    );
                    BinaryContent savedBinaryContent = binaryContentRepository.save(bc);
                    binaryContentStorage.put(savedBinaryContent.getId(), file.getBytes());
                    user.updateProfile(savedBinaryContent);
                    log.debug("프로필 이미지 저장 성공 - fileName: {}", file.getOriginalFilename());
                 } catch (IOException e){
                     log.error("프로필 이미지 저장 실패 - fileName: {}", file.getOriginalFilename(), e);
                     throw new RuntimeException("파일 처리 실패" + e.getMessage());
                 }
                });
        UserStatus userStatus = new UserStatus(user);

        User savedUser = userRepository.save(user);
        log.info("사용자 생성 성공 - userId: {}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO find(UUID userId) {
        log.debug("사용자 단건 조회 요청 - userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패 - 존재하지 않는 userId: {}", userId);
                    return new UserNotFoundException(userId);
                });
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.debug("전체 사용자 목록 조회 요청");
        return userRepository.findAllWithProfileAndStatus().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    // 이름. 프로필 선택적 업데이트
    @Override
    @Transactional
    public UserDTO update(UUID userID, UserUpdateRequest request, Optional<MultipartFile> profile) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> {
                    log.warn("사용자 수정 실패 - 존재하지 않는 userId: {}", userID);
                    return new UserNotFoundException(userID);
                });

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
                        log.debug("프로필 이미지 수정 - fileName: {}", file.getOriginalFilename());
                        BinaryContent bc = new BinaryContent(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getSize()
                        );
                        BinaryContent savedBinaryContent = binaryContentRepository.save(bc);
                        binaryContentStorage.put(savedBinaryContent.getId(), file.getBytes());
                        user.updateProfile(savedBinaryContent);
                        log.debug("프로필 이미지 수정 성공 - userId:{}, fileName: {}", userID, file.getOriginalFilename());
                    } catch (IOException e){
                        log.error("프로필 이미지 수정 실패 - fileName: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("파일 처리 실패" + e.getMessage());
                    }
                });

        log.info("사용자 수정 성공 - userId: {}", userID);
        return userMapper.toDTO(user);
    }

    // user가 해당 ch에서 보낸 msg 삭제 반영 X
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 삭제 실패 - 존재하지 않는 userId: {}", userId);
                    return new UserNotFoundException("User not found", userId);
                });

        userRepository.deleteById(user.getId());
        log.info("사용자 삭제 성공 - userId: {}", userId);
    }

    // User 이름 유효성 검증
    public void validateName(String username){
        if(userRepository.existsByUsername(username)){
            log.warn("사용자 생성/수정 실패 - 이미 존재하는 username: {}", username);
            throw new AlreadyExistsNameException(username);
        }
    }

    // 이메일 유효성 검증
    public void validateEmail(String email){
        if(userRepository.existsByEmail(email)){
            log.warn("사용자 생성/수정 실패 - 이미 존재하는 email: {}", email);
            throw new AlreadyExistsEmailException(email);
        }
    }
}
