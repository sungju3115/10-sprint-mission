package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 프레임 헤더에서 Authorization 토큰 추출
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
                throw new MessageDeliveryException("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
            }

            String token = authorization.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
            }

            // 토큰에서 userId 추출 → UserDetails 조회
            UUID userId = jwtTokenProvider.getUserId(token);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            // 인증 객체 생성 후 SecurityContext 대신 accessor에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            accessor.setUser(authentication);
        }

        return message;
    }
}
