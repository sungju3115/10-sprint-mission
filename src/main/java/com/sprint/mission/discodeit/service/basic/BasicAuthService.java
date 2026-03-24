package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.dto.auth.request.AuthServiceRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final AuthMapper authMapper;
    @Transactional(readOnly = true)
    public UserDTO login(AuthServiceRequest request){
        // name 같은 지 확인
        User user = userRepository.findByUsernameWithProfile(request.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.username()));

        // password 같은지 확인
        if(!(user.getPassword().equals(request.password()))){
            throw new IllegalArgumentException("Wrong password");
        }
        // 유저 정보 반환, DTO로 ??
        return authMapper.toResponse(user);
    }
}
