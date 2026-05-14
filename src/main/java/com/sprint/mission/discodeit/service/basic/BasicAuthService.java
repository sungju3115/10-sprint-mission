package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.login.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

//    @Transactional(readOnly = true)
//    public UserDTO login(AuthServiceRequest request){
//        User user = userRepository.findByUsernameWithProfile(request.username())
//                .orElseThrow(() -> new UserNotFoundException(request.username()));
//
//        // 해시 비교로 교체
//        if(!passwordEncoder.matches(request.password(), user.getPassword())){
//            throw new InvalidPasswordException(request.username());
//        }
//
//        log.info("로그인 성공 - username: {}", request.username());
//        return authMapper.toResponse(user);
//    }
}
