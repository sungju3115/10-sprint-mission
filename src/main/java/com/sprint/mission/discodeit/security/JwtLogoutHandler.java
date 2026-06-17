package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        // REFRESH_TOKEN 쿠키를 만료시켜 삭제
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // JWT 레지스트리에서 제거 → 내부에서 UserLogInOutEvent 발행 → SSE online=false 브로드캐스트
        if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDTO().id());
        }
    }
}
