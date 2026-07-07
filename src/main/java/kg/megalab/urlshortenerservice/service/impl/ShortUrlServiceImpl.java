package kg.megalab.urlshortenerservice.service.impl;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.entity.ShortUrl;
import kg.megalab.urlshortenerservice.exception.ShortCodeNotFoundException;
import kg.megalab.urlshortenerservice.mapper.ShortUrlMapper;
import kg.megalab.urlshortenerservice.repository.ShortUrlRepository;
import kg.megalab.urlshortenerservice.service.ShortCodeGenerator;
import kg.megalab.urlshortenerservice.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository repository;
    private final ShortCodeGenerator generator;
    private final ShortUrlMapper mapper;

    @Transactional
    @Override
    public ShortUrlResponse create(CreateShortUrlRequest request) {

        String shortCode = generateUniqueShortCode();

        ShortUrl shortUrl = mapper.toEntity(request, shortCode);

        ShortUrl saved = repository.save(shortUrl);

        return mapper.toResponse(saved);
    }

    @Transactional
    @Override
    public String resolveShortCode(String shortCode) {

        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(ShortCodeNotFoundException::new);

        if(Instant.now().isAfter(shortUrl.getExpiresAt()))
        {
            throw new ShortCodeNotFoundException();
        }

        shortUrl.incrementClickCount();

        return shortUrl.getOriginalUrl();
    }



    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = generator.generate();
        } while (repository.existsByShortCode(shortCode));

        return shortCode;
    }
}
