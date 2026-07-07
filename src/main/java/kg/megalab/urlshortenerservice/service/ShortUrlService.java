package kg.megalab.urlshortenerservice.service;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;


public interface ShortUrlService {

    ShortUrlResponse create(CreateShortUrlRequest request);

    String resolveShortCode(String shortCode);
}
