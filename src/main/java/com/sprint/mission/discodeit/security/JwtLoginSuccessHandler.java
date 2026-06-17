package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

        var user = principal.getUserDTO();

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(
                user.id(), user.username(), user.role().name());

        // 리프레시 토큰 생성 후 쿠키에 저장
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.id());
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        // 응답 Body: accessToken
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new JwtDto(user, accessToken, refreshToken));

        // JWT 레지스트리에 등록 → 내부에서 UserLogInOutEvent 발행 → SSE online=true 브로드캐스트
        jwtRegistry.registerJwtInformation(new JwtInformation(user, accessToken, refreshToken));
    }
}
