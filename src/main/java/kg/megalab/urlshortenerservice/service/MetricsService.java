package kg.megalab.urlshortenerservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter urlCreatedCounter;
    private final Counter redirectCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public MetricsService(MeterRegistry meterRegistry) {
        this.urlCreatedCounter = meterRegistry.counter("url.creation");
        this.redirectCounter = meterRegistry.counter("url.redirect");
        this.cacheHitCounter = meterRegistry.counter("url.cache.hit");
        this.cacheMissCounter = meterRegistry.counter("url.cache.miss");

    }

    public void incrementUrlCreated() {
        urlCreatedCounter.increment();
    }

    public void incrementRedirect() {
        redirectCounter.increment();
    }

    public void incrementCacheHit() {
        cacheHitCounter.increment();
    }

    public void incrementCacheMiss() {
        cacheMissCounter.increment();
    }
}
