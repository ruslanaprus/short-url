package org.goit.urlshortener.service.url;

import org.goit.urlshortener.exceptionHandler.ShortUrlException;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.request.UrlCreateRequest;
import org.goit.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.goit.urlshortener.exceptionHandler.ExceptionMessages.URL_EXPIRED;
import static org.goit.urlshortener.exceptionHandler.ExceptionMessages.URL_NOT_FOUND;
import static org.goit.urlshortener.exceptionHandler.ExceptionMessages.URL_NOT_FOUND_OR_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        UrlCreateRequest request = new UrlCreateRequest(originalUrl, null); // No custom shortCode

        Url expectedUrl = new Url();
        expectedUrl.setId(1L);
        expectedUrl.setOriginalUrl(originalUrl);
        expectedUrl.setShortCode("testShortCode");
        expectedUrl.setUser(user);

        when(generator.generateUniqueShortCode(any())).thenReturn("testShortCode");
        when(urlRepository.save(any(Url.class))).thenReturn(expectedUrl);

        Url url = urlService.createUrl(request, user);

        assertNotNull(url);
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("testShortCode", url.getShortCode());
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
        url.setShortCode("testShortCode");
        url.setClickCount(0L);

        when(urlRepository.findByShortCode("testShortCode")).thenReturn(Optional.of(url));
        when(urlRepository.save(any())).thenReturn(url);

        urlService.incrementClickCount(url);

        assertEquals(1L, url.getClickCount(), "Click count should be incremented by 1");
        verify(urlRepository).save(url);
    }

    @Test
    @DisplayName("Expired URL should throw exception when fetched as valid")
    void testGetValidUrlWithExpiredUrl() {
        Url expiredUrl = new Url();
        expiredUrl.setShortCode("testShortCode");
        expiredUrl.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(urlRepository.findByShortCode("testShortCode")).thenReturn(Optional.of(expiredUrl));

        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> urlService.getValidUrl("testShortCode"));

        assertEquals(URL_EXPIRED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Non-expired URL should be fetched as valid")
    void testGetValidUrlWithNonExpiredUrl() {
        Url validUrl = new Url();
        validUrl.setShortCode("testShortCode");
        validUrl.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(urlRepository.findByShortCode("testShortCode")).thenReturn(Optional.of(validUrl));

        Url result = assertDoesNotThrow(() -> urlService.getValidUrl("testShortCode"));
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

        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> urlService.deleteUrl(1L, otherUser));
        assertEquals(URL_NOT_FOUND.getMessage(), exception.getMessage());
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
    @DisplayName("Fetching valid URL without expiry should work correctly")
    void testGetValidUrlWithoutExpiry() {
        Url url = new Url();
        url.setShortCode("testShortCode");
        url.setExpiresAt(null);

        when(urlRepository.findByShortCode("testShortCode")).thenReturn(Optional.of(url));

        Url result = assertDoesNotThrow(() -> urlService.getValidUrl("testShortCode"));
        assertEquals(url, result);
    }

    @Test
    @DisplayName("Fetching non-existent URL should throw exception")
    void testGetNonExistentUrl() {
        when(urlRepository.findByShortCode("testShortCode")).thenReturn(Optional.empty());

        ShortUrlException exception = assertThrows(ShortUrlException.class,
                () -> urlService.getValidUrl("testShortCode"));
        assertEquals("URL not found", exception.getMessage());
    }
}
