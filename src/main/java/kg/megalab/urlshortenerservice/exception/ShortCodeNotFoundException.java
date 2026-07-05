package kg.megalab.urlshortenerservice.exception;

public class ShortCodeNotFoundException extends RuntimeException {
    public ShortCodeNotFoundException() {
        super("URL not found");
    }
}
