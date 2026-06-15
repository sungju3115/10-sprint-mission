package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.service.SseService;
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

    private final SseService sseService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        // REFRESH_TOKEN 쿠키를 만료시켜 삭제
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        // 로그아웃 시 online=false로 SSE 브로드캐스트
        if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            UserDTO user = userDetails.getUserDTO();
            UserDTO offlineUser = new UserDTO(user.id(), user.username(), user.email(), user.profile(), false, user.role());
            sseService.broadcast("users.updated", offlineUser);
        }
    }
}
