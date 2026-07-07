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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    static final Logger log = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    private final ShortUrlRepository repository;
    private final ShortCodeGenerator generator;
    private final ShortUrlMapper mapper;
    private final RedisUrlCacheService cacheService;

    @Transactional
    @Override
    public ShortUrlResponse create(CreateShortUrlRequest request) {

        String shortCode = generateUniqueShortCode();

        ShortUrl shortUrl = mapper.toEntity(request, shortCode);

        ShortUrl saved = repository.save(shortUrl);

        log.info("Short URL created: {}", shortCode);

        return mapper.toResponse(saved);
    }

    @Override
    public List<ShortUrlResponse> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public ShortUrlResponse findById(Long id) {
        return null;
    }

    @Override
    public ShortUrl getByShortCode(String shortCode) {
        return null;
    }

    @Transactional
    @Override
    public String resolveShortCode(String shortCode) {

        Optional<String> cachedUrl = cacheService.get(shortCode);

        if(cachedUrl.isPresent()){

            repository.incrementClickCount(shortCode);
            log.info("Cache hit for {}", shortCode);
            return cachedUrl.get();
        }

        log.info("Cache miss for {}", shortCode);

        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(ShortCodeNotFoundException::new);

        Instant now = Instant.now();

        if(now.isAfter(shortUrl.getExpiresAt()))
        {
            throw new ShortCodeNotFoundException();
        }

        Duration duration = Duration.between( now, shortUrl.getExpiresAt() );

        cacheService.saveUrlByShortCode( shortCode, shortUrl.getOriginalUrl(), duration );

        repository.incrementClickCount(shortCode);

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
