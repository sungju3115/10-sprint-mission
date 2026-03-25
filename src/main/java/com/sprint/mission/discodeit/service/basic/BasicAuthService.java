package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Transactional(readOnly = true)
    public UserDTO login(AuthServiceRequest request){
        // name 같은 지 확인
        User user = userRepository.findByUsernameWithProfile(request.username())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + request.username()));

        // password 같은지 확인
        if(!(user.getPassword().equals(request.password()))){
            throw new IllegalArgumentException("Wrong password");
        }
        // 유저 dto로 변환
        return authMapper.toResponse(user);
    }
}
