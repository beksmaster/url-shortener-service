package kg.megalab.urlshortenerservice.dto.response;

import java.time.Instant;

public record ShortUrlResponse(
    Long id,
    String originalUrl,
    String shortCode,
    String shortUrl,
    Instant createdAt,
    Instant expiresAt,
    Long clickCount
) {
    public ShortUrlResponse withShortUrl(String shortUrl) {
        return new ShortUrlResponse(
                id,
                originalUrl,
                shortCode,
                shortUrl,
                createdAt,
                expiresAt,
                clickCount
        );
    }
}
