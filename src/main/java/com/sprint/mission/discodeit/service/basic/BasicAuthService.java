package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JwtRegistry jwtRegistry;

    @Override
    @Transactional
    public UserDTO updateRole(RoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        applicationEventPublisher.publishEvent(new RoleUpdatedEvent(
                user.getId(),
                user.getRole().toString(),
                request.newRole().toString()
        ));

        user.updateRole(request.newRole());

        return userMapper.toDTO(user);
    }

    @Override
    public JwtDto refresh(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        // 레지스트리에 등록된 토큰인지 확인 (로그아웃된 토큰 차단)
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료되거나 무효화된 리프레시 토큰입니다.");
        }

        // 토큰에서 userId 추출 후 사용자 조회
        java.util.UUID userId = jwtTokenProvider.getUserId(refreshToken);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserById(userId);
        var user = userDetails.getUserDTO();

        // 새 액세스 토큰 + 새 리프레시 토큰 발급 (Rotation)
        String newAccessToken = jwtTokenProvider.generateToken(user.id(), user.username(), user.role().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        // 레지스트리에서 기존 토큰을 새 토큰으로 교체
        JwtInformation newJwtInfo = new JwtInformation(user, newAccessToken, newRefreshToken);
        jwtRegistry.rotateJwtInformation(refreshToken, newJwtInfo);

        log.debug("토큰 재발급 성공 - userId: {}", userId);
        return new JwtDto(user, newAccessToken, newRefreshToken);
    }
}
