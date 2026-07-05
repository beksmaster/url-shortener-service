package kg.megalab.urlshortenerservice.service.impl;

import kg.megalab.urlshortenerservice.service.UrlCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RedisUrlCacheService implements UrlCacheService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public Optional<String> get(String shortCode) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(shortCode)
        );
    }

    @Override
    public void saveUrlByShortCode(String shortCode, String originalUrl, Duration ttl) {
        redisTemplate.opsForValue().set(shortCode, originalUrl, ttl);
    }
}
