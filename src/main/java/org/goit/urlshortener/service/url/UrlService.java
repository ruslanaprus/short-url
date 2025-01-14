package org.goit.urlshortener.service.url;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlValidationService urlValidator;
    private final ShortCodeGenerator shortCodeGenerator;

    @Value("${url.expiry.default-days:1}")
    private int defaultExpiryDays;

    public Page<Url> findUrlsByUser(@NotNull User user, Pageable pageable) {
        log.info("Fetching URLs for user with id={}, pageable={}", user.getId(), pageable);
        return urlRepository.findByUser(user, pageable);
    }

    public Page<Url> findUrlsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching URLs for userId={}, pageable={}", userId, pageable);
        return urlRepository.findByUserId(userId, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Url createUrl(String originalUrl, @NotNull User currentUser) {
        log.info("Creating a new URL for user with id={}", currentUser.getId());
        urlValidator.validateUrl(originalUrl);
        log.debug("URL validation passed: {}", originalUrl);

        String shortCode = shortCodeGenerator.generateUniqueShortCode(urlRepository::existsByShortCode);
        log.debug("Generated unique shortCode: {}", shortCode);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusDays(defaultExpiryDays);

        Url newUrl = Url.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .clickCount(0L)
                .user(currentUser)
                .build();

        Url savedUrl = urlRepository.save(newUrl);
        log.info("URL saved successfully: id={}, shortCode={}, for user with id={}",
                savedUrl.getId(), savedUrl.getShortCode(), currentUser.getId());
        return savedUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUrl(Long urlId, @NotNull User currentUser) {
        log.info("Request to delete URL with id={} by user with id={}", urlId, currentUser.getId());

        Url url = urlRepository.findByIdAndUser(urlId, currentUser)
                .orElseThrow(() -> new RuntimeException("URL not found or user not authorized to delete it"));

        urlRepository.delete(url);
        log.info("URL with id={} was deleted by user with id={}", urlId, currentUser.getId());
    }

    public Optional<Url> findByShortCode(String shortCode) {
        log.info("Fetching URL by shortCode={}", shortCode);
        return urlRepository.findByShortCode(shortCode);
    }

    public Optional<String> findOriginalUrlByShortCode(String shortCode) {
        log.info("Fetching original URL by shortCode={}", shortCode);
        return urlRepository.findOriginalUrlByShortCode(shortCode);
    }

    public Optional<Url> findByIdAndUser(Long id, @NotNull User user) {
        log.info("Fetching URL with id={} for user with id={}", id, user.getId());
        return urlRepository.findByIdAndUser(id, user);
    }

    public Url getValidUrl(String shortCode) {
        log.info("Fetching valid URL by shortCode={}", shortCode);
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("URL not found or shortCode is invalid: {}", shortCode);
                    return new RuntimeException("URL not found or invalid shortCode");
                });

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("URL with shortCode={} has expired", shortCode);
            throw new RuntimeException("This URL has expired");
        }

        log.info("URL with shortCode={} is valid", shortCode);
        return url;
    }

    @Transactional
    public Url incrementClickCount(String shortCode) {
        log.info("Request to increment clickCount for URL with shortCode={}", shortCode);
        Url url = getValidUrl(shortCode);
        url.setClickCount(url.getClickCount() + 1);
        Url updatedUrl = urlRepository.save(url);
        log.info("ClickCount for URL with shortCode={} incremented to {}", shortCode, updatedUrl.getClickCount());
        return updatedUrl;
    }

    public boolean isUrlActive(Long urlId, LocalDateTime now) {
        log.info("Checking if URL with id={} is active at {}", urlId, now);
        return urlRepository.existsActiveUrlById(urlId, now);
    }
}
