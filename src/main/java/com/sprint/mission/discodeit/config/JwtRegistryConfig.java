package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.security.InMemoryJwtRegistry;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.RedisJwtRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class JwtRegistryConfig {

    @Bean
    @Profile("!prod")
    public JwtRegistry inMemoryJwtRegistry(
            JwtTokenProvider jwtTokenProvider,
            ApplicationEventPublisher eventPublisher) {
        return new InMemoryJwtRegistry(jwtTokenProvider, eventPublisher);
    }

    @Bean
    @Profile("prod")
    public JwtRegistry redisJwtRegistry(
            JwtTokenProvider jwtTokenProvider,
            ApplicationEventPublisher eventPublisher,
            RedisTemplate<String, Object> redisTemplate,
            RedisLockProvider redisLockProvider) {
        return new RedisJwtRegistry(5, jwtTokenProvider, eventPublisher, redisTemplate, redisLockProvider);
    }
}
