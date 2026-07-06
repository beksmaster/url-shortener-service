package kg.megalab.urlshortenerservice.mapper;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.entity.ShortUrl;
import org.springframework.stereotype.Component;

@Component
public class ShortUrlMapper {

    public ShortUrlResponse toResponse(ShortUrl shortUrl){
        return new ShortUrlResponse(
                shortUrl.getId(),
                shortUrl.getOriginalUrl(),
                shortUrl.getShortCode(),
                null,
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.getClickCount()
        );
    }

    public ShortUrl toEntity(CreateShortUrlRequest request, String shortCode) {

        return ShortUrl.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .expiresAt(request.expiresAt())
                .build();
    }
}
