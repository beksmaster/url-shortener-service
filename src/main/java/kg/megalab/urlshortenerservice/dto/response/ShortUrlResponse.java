package kg.megalab.urlshortenerservice.dto.response;

import java.time.Instant;

public record ShortUrlResponse(
    Long id,
    String originalUrl,
    String shortCode,
    Instant createdAt,
    Instant expiresAt,
    Long clickCount
) {
}
