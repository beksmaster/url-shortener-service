package kg.megalab.urlshortenerservice.exception;

public record ErrorResponse(
        java.time.Instant timestamp,
        int status,
        String message
) {
}
