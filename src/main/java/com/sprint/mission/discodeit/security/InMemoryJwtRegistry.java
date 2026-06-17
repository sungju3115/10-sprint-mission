package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private static final int MAX_ACTIVE_JWT_COUNT = 5;

    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<UUID, List<JwtInformation>> store = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndex = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndex = ConcurrentHashMap.newKeySet();

    @CacheEvict(value = "users", key = "'all'")
    @Override
    public synchronized void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        List<JwtInformation> tokens = store.computeIfAbsent(userId, k -> new ArrayList<>());

        while (tokens.size() >= MAX_ACTIVE_JWT_COUNT) {
            JwtInformation oldest = tokens.remove(0);
            accessTokenIndex.remove(oldest.getAccessToken());
            refreshTokenIndex.remove(oldest.getRefreshToken());
        }

        tokens.add(jwtInformation);
        accessTokenIndex.add(jwtInformation.getAccessToken());
        refreshTokenIndex.add(jwtInformation.getRefreshToken());

        eventPublisher.publishEvent(new UserLogInOutEvent(userId, true));
    }

    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        List<JwtInformation> tokens = store.remove(userId);
        if (tokens != null) {
            tokens.forEach(t -> {
                accessTokenIndex.remove(t.getAccessToken());
                refreshTokenIndex.remove(t.getRefreshToken());
            });
        }
        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        List<JwtInformation> tokens = store.get(userId);
        return tokens != null && !tokens.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndex.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndex.contains(refreshToken);
    }

    @Override
    public synchronized void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();
        List<JwtInformation> tokens = store.get(userId);
        if (tokens == null) return;

        for (JwtInformation token : tokens) {
            if (token.getRefreshToken().equals(refreshToken)) {
                accessTokenIndex.remove(token.getAccessToken());
                refreshTokenIndex.remove(token.getRefreshToken());
                token.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                accessTokenIndex.add(token.getAccessToken());
                refreshTokenIndex.add(token.getRefreshToken());
                break;
            }
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        store.forEach((userId, tokens) ->
            tokens.removeIf(token -> {
                boolean expired = !jwtTokenProvider.validateAccessToken(token.getAccessToken()) ||
                        !jwtTokenProvider.validateRefreshToken(token.getRefreshToken());
                if (expired) {
                    accessTokenIndex.remove(token.getAccessToken());
                    refreshTokenIndex.remove(token.getRefreshToken());
                }
                return expired;
            })
        );
        store.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
