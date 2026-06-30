package kg.megalab.urlshortenerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateShortUrlRequest(
        @NotBlank
        @Size(max = 2048)
        String originalUrl,
        Instant expiresAt
) {
}
