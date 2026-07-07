package kg.megalab.urlshortenerservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;

public record CreateShortUrlRequest(
        @URL
        @NotBlank
        @Size(max = 2048)
        String originalUrl,

        @NotNull
        @Future
        Instant expiresAt
) {
}
