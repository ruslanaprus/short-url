package org.goit.urlshortener.service.url;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlServiceTest {

    private final UrlRepository urlRepository = mock(UrlRepository.class);
    private final UrlValidationService validator = mock(UrlValidationService.class);
    private final ShortCodeGenerator generator = mock(ShortCodeGenerator.class);

    private final UrlService urlService = new UrlService(urlRepository, validator, generator);

    @Test
    @DisplayName("Creating a valid URL should return a saved URL")
    void testCreateUrl() {
        User user = new User();
        user.setIdForTest(1L);
        String originalUrl = "https://example.com";

        Url expectedUrl = new Url();
        expectedUrl.setId(1L);
        expectedUrl.setOriginalUrl(originalUrl);
        expectedUrl.setShortCode("abc123");
        expectedUrl.setUser(user);

        when(generator.generateUniqueShortCode(any())).thenReturn("abc123");
        when(urlRepository.save(any(Url.class))).thenReturn(expectedUrl);

        Url url = urlService.createUrl(originalUrl, user);

        assertNotNull(url);
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(1L, url.getId());
    }

    @Test
    @DisplayName("Deleting a URL should not throw exceptions for valid user")
    void testDeleteUrl() {
        User user = new User();
        user.setIdForTest(1L);

        Url url = new Url();
        url.setId(1L);
        url.setUser(user);

        when(urlRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(url));
        doNothing().when(urlRepository).delete(url);

        assertDoesNotThrow(() -> urlService.deleteUrl(1L, user));
    }

    @Test
    @DisplayName("Incrementing click count should update the URL click count")
    void testIncrementClickCount() {
        Url url = new Url();
        url.setShortCode("abc123");
        url.setClickCount(0L);

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(urlRepository.save(any())).thenReturn(url);

        Url updatedUrl = urlService.incrementClickCount("abc123");
        assertEquals(1L, updatedUrl.getClickCount());
    }

    @Test
    @DisplayName("Expired URL should throw exception when fetched as valid")
    void testGetValidUrlWithExpiredUrl() {
        Url expiredUrl = new Url();
        expiredUrl.setShortCode("abc123");
        expiredUrl.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(expiredUrl));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urlService.getValidUrl("abc123"));
        assertEquals("This URL has expired", exception.getMessage());
    }

    @Test
    @DisplayName("Non-expired URL should be fetched as valid")
    void testGetValidUrlWithNonExpiredUrl() {
        Url validUrl = new Url();
        validUrl.setShortCode("abc123");
        validUrl.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(validUrl));

        Url result = assertDoesNotThrow(() -> urlService.getValidUrl("abc123"));
        assertEquals(validUrl, result);
    }

    @Test
    @DisplayName("Deleting another user's URL should throw exception")
    void testDeleteOtherUserUrl() {
        User owner = new User();
        owner.setIdForTest(1L);

        User otherUser = new User();
        otherUser.setIdForTest(2L);

        Url url = new Url();
        url.setId(1L);
        url.setUser(owner);

        when(urlRepository.findByIdAndUser(1L, otherUser)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urlService.deleteUrl(1L, otherUser));
        assertEquals("URL not found or user not authorized to delete it", exception.getMessage());
    }

    @Test
    @DisplayName("Fetching URLs by user should return paginated results")
    void testFindUrlsByUser() {
        User user = new User();
        user.setIdForTest(1L);

        Pageable pageable = Pageable.ofSize(10);
        Page<Url> mockPage = mock(Page.class);

        when(urlRepository.findByUser(user, pageable)).thenReturn(mockPage);

        Page<Url> result = urlService.findUrlsByUser(user, pageable);

        assertNotNull(result);
        assertEquals(mockPage, result);
        verify(urlRepository).findByUser(user, pageable);
    }

    @Test
    @DisplayName("Fetching original URL by short code should return correct URL")
    void testFindOriginalUrlByShortCode() {
        when(urlRepository.findOriginalUrlByShortCode("abc123")).thenReturn(Optional.of("https://example.com"));

        Optional<String> originalUrl = urlService.findOriginalUrlByShortCode("abc123");
        assertTrue(originalUrl.isPresent());
        assertEquals("https://example.com", originalUrl.get());
    }

    @Test
    @DisplayName("Fetching valid URL without expiry should work correctly")
    void testGetValidUrlWithoutExpiry() {
        Url url = new Url();
        url.setShortCode("abc123");
        url.setExpiresAt(null); // No expiry

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        Url result = assertDoesNotThrow(() -> urlService.getValidUrl("abc123"));
        assertEquals(url, result);
    }

    @Test
    @DisplayName("Fetching non-existent URL should throw exception")
    void testGetNonExistentUrl() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urlService.getValidUrl("abc123"));
        assertEquals("URL not found or invalid shortCode", exception.getMessage());
    }
}
