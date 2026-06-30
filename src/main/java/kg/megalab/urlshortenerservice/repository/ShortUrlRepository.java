package kg.megalab.urlshortenerservice.repository;

import kg.megalab.urlshortenerservice.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    <Optional>ShortUrl findByShortCode (String shortCode);

    boolean existsByShortCode (String shortCode);
}
