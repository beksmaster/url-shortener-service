package kg.megalab.urlshortenerservice.service.impl;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.entity.ShortUrl;
import kg.megalab.urlshortenerservice.mapper.ShortUrlMapper;
import kg.megalab.urlshortenerservice.repository.ShortUrlRepository;
import kg.megalab.urlshortenerservice.service.ShortCodeGenerator;
import kg.megalab.urlshortenerservice.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public String resolveShortCode(String shortCode) {
        return repository.findByShortCode(shortCode).getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = generator.generate();
        } while (repository.existsByShortCode(shortCode));

        return shortCode;
    }
}
