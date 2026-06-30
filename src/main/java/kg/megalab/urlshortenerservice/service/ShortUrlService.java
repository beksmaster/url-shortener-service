package kg.megalab.urlshortenerservice.service;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.entity.ShortUrl;

import java.util.List;

public interface ShortUrlService {

    ShortUrlResponse create(CreateShortUrlRequest request);

    List<ShortUrlResponse> findAll();

    void deleteById(Long id);

    ShortUrlResponse findById(Long id);

    ShortUrl getByShortCode(String shortCode);
}
