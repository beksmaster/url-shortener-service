package kg.megalab.urlshortenerservice.integration;

import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.entity.ShortUrl;
import kg.megalab.urlshortenerservice.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ShortUrlIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    ShortUrlRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateShortUrl() throws Exception{

        CreateShortUrlRequest request = defaultRequest();

        mockMvc.perform(
                post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)
                        )
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.originalUrl").value(request.originalUrl()))
                .andExpect(jsonPath("$.expiresAt").value(request.expiresAt().toString()))
                .andExpect(jsonPath("$.shortCode").isNotEmpty())
                .andExpect(jsonPath("$.shortUrl").isNotEmpty())
                .andExpect(jsonPath("$.shortUrl").value(
                        org.hamcrest.Matchers.endsWith(
                                "/" + repository.findAll().getFirst().getShortCode()
                        )
                ));

        ShortUrl shortUrl = repository.findAll().getFirst();

        assertEquals(request.originalUrl(), shortUrl.getOriginalUrl());
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception{

        CreateShortUrlRequest request = defaultRequest();

        ShortUrlResponse response =
                createShortUrl(request);

        mockMvc.perform(
                get("/" + response.shortCode())
        )
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, request.originalUrl()));

        ShortUrl shortUrl =
                repository.findByShortCode(response.shortCode()).orElseThrow();

        assertEquals(1, shortUrl.getClickCount());

    }

    @Test
    void shouldReturn404ForUnknownShortCode() throws Exception{
        mockMvc.perform(
                get("/unknownCode")
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldResolveUrlFromRedisCache() throws Exception {

        CreateShortUrlRequest request = defaultRequest();

        ShortUrlResponse response =
                createShortUrl(request);

        mockMvc.perform(
                        get("/" + response.shortCode())
                )
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, request.originalUrl()));

        mockMvc.perform(
                        get("/" + response.shortCode())
                )
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, request.originalUrl()));

        String cachedUrl =
                redisTemplate.opsForValue().get(response.shortCode());

        assertEquals(
                request.originalUrl(),
                cachedUrl
        );

        ShortUrl shortUrl =
                repository.findByShortCode(response.shortCode())
                        .orElseThrow();

        assertEquals(2, shortUrl.getClickCount());

    }

    private ShortUrlResponse createShortUrl(CreateShortUrlRequest request) throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)
                                )

                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        return objectMapper.readValue(json, ShortUrlResponse.class);
    }

    private CreateShortUrlRequest defaultRequest() {
        return new CreateShortUrlRequest(
                "https://roadmap.sh/java",
                Instant.now().plus(Duration.ofDays(30))
        );
    }
}
