package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Transactional(readOnly = true)
    public UserDTO login(AuthServiceRequest request){
        log.info("로그인 시도 - username: {}", request.username());
        User user = userRepository.findByUsernameWithProfile(request.username())
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 사용자: {}", request.username());
                    return new NoSuchElementException("User not found: " + request.username());
                });

        if(!(user.getPassword().equals(request.password()))){
            log.warn("로그인 실패 - 비밀번호 불일치: {}", request.username());
            throw new IllegalArgumentException("Wrong password");
        }

        log.info("로그인 성공 - username: {}", request.username());
        return authMapper.toResponse(user);
    }
}
