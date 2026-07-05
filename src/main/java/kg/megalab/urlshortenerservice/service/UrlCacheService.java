package kg.megalab.urlshortenerservice.service;

import java.time.Duration;
import java.util.Optional;

public interface UrlCacheService {

    Optional <String> get(String shortCode);

    void saveUrlByShortCode(String shortCode, String originalUrl, Duration ttl);

}
