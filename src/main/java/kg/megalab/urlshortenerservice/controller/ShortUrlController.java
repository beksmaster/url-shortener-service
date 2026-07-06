package kg.megalab.urlshortenerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import kg.megalab.urlshortenerservice.dto.request.CreateShortUrlRequest;
import kg.megalab.urlshortenerservice.dto.response.ShortUrlResponse;
import kg.megalab.urlshortenerservice.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @Operation(summary = "Create a short URL")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("")
    public ResponseEntity<ShortUrlResponse> create(
            @Valid @RequestBody CreateShortUrlRequest request) {

        ShortUrlResponse response = shortUrlService.create(request);

        String shortUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{shortCode}")
                .buildAndExpand(response.shortCode())
                .toUriString();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response.withShortUrl(shortUrl));
    }

    @Operation(summary = "Get url by short code")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getUrl(@PathVariable String shortCode) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(shortUrlService.resolveShortCode(shortCode))).build();
    }
}
