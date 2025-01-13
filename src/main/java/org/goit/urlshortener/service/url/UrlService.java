package org.goit.urlshortener.service.url;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UrlRepository;
import org.goit.urlshortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.InvalidUrlException;

import javax.management.timer.Timer;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final UrlValidator urlValidator;
    private final ShortCodeGenerator shortCodeGenerator;

    @Value("${url.expiry.default-days:1}")
    private int defaultExpiryDays;

    @Transactional(rollbackFor = Exception.class)
    public Url createUrl(String originalUrl, Long userId) {
        if (!urlValidator.isValidUrl(originalUrl)) {
            throw new InvalidUrlException("Invalid format URL");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusDays(defaultExpiryDays);
        Long clickCount = 0L;

        Url newUrl = Url.builder()
                .createdAt(createdAt)
                .clickCount(clickCount)
                .expiresAt(expiresAt)
                .originalUrl(originalUrl)
                .shortCode(shortCodeGenerator.generateUniqueShortCode(
                        () -> urlRepository.existsByShortCode(originalUrl)
                ))
                .user(user)
                .build();

        return urlRepository.save(newUrl);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUrl(Long urlId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (!url.getUser().equals(user)) {
            throw new RuntimeException("User is not authorized to delete this URL");
        }

        urlRepository.delete(url);
    }


    public Page<Url> findUrlsByUserId(Long userId, Pageable pageable) {
        return urlRepository.findByUserId(userId, pageable);
    }
}
