package org.goit.urlshortener.url.service;

import org.goit.urlshortener.common.exception.ShortUrlException;
import org.goit.urlshortener.url.model.Url;
import org.goit.urlshortener.auth.model.User;
import org.goit.urlshortener.url.model.dto.UrlCreateRequest;
import org.goit.urlshortener.url.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.goit.urlshortener.common.exception.ExceptionMessages.*;
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
        assertEquals(URL_NOT_FOUND_OR_UNAUTHORIZED.getMessage(), exception.getMessage());
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

    @Test
    @DisplayName("Should return URL when shortCode is found")
    void testFindByShortCode_Success() {
        // Arrange
        String shortCode = "abc123";
        Url expectedUrl = new Url();
        expectedUrl.setShortCode(shortCode);
        expectedUrl.setOriginalUrl("http://example.com");

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(expectedUrl));

        // Act
        Url result = urlService.findByShortCode(shortCode);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedUrl, result, "The returned URL should match the expected URL");
        verify(urlRepository, times(1)).findByShortCode(shortCode);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should throw ShortUrlException when shortCode is not found")
    void testFindByShortCode_NotFound() {
        // Arrange
        String shortCode = "nonexistent";

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // Act & Assert
        ShortUrlException exception = assertThrows(ShortUrlException.class, () -> {
            urlService.findByShortCode(shortCode);
        });

        assertEquals("URL not found or user not authorized", exception.getMessage(), "Exception message should match");
        verify(urlRepository, times(1)).findByShortCode(shortCode);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should list active URLs by user")
    void testListUrlsByStatus_Active() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").build();

        Pageable pageable = mock(Pageable.class);
        List<Url> urls = List.of(new Url(), new Url());
        Page<Url> expectedPage = new PageImpl<>(urls);

        when(urlRepository.findActiveUrlsByUser(user, pageable)).thenReturn(expectedPage);

        // Act
        Page<Url> result = urlService.listUrlsByStatus(user, "active", pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedPage, result, "The returned page should match the expected page");
        verify(urlRepository, times(1)).findActiveUrlsByUser(user, pageable);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should list expired URLs by user")
    void testListUrlsByStatus_Expired() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").build();

        Pageable pageable = mock(Pageable.class);
        List<Url> urls = List.of(new Url(), new Url());
        Page<Url> expectedPage = new PageImpl<>(urls);

        when(urlRepository.findExpiredUrlsByUser(user, pageable)).thenReturn(expectedPage);

        // Act
        Page<Url> result = urlService.listUrlsByStatus(user, "expired", pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedPage, result, "The returned page should match the expected page");
        verify(urlRepository, times(1)).findExpiredUrlsByUser(user, pageable);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should list all URLs by user")
    void testListUrlsByStatus_All() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").build();

        Pageable pageable = mock(Pageable.class);
        List<Url> urls = List.of(new Url(), new Url());
        Page<Url> expectedPage = new PageImpl<>(urls);

        when(urlRepository.findByUser(user, pageable)).thenReturn(expectedPage);

        // Act
        Page<Url> result = urlService.listUrlsByStatus(user, "all", pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedPage, result, "The returned page should match the expected page");
        verify(urlRepository, times(1)).findByUser(user, pageable);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid status")
    void testListUrlsByStatus_InvalidStatus() {
        // Arrange
        User user = User.builder().id(1L).email("test@example.com").build();

        Pageable pageable = mock(Pageable.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlService.listUrlsByStatus(user, "invalid", pageable);
        });

        assertEquals("Invalid status: invalid", exception.getMessage(), "Exception message should match");
    }

    @Test
    @DisplayName("Should update URL when valid inputs are provided")
    void testUpdateUrl_Success() {
        // Arrange
        Long urlId = 1L;
        User user = User.builder().id(1L).email("test@example.com").build();

        Url existingUrl = new Url();
        existingUrl.setId(urlId);
        existingUrl.setOriginalUrl("http://old.com");
        existingUrl.setShortCode("abc123");

        Url updatedData = new Url();
        updatedData.setOriginalUrl("http://new.com");
        updatedData.setShortCode("xyz789");

        when(urlRepository.findByIdAndUser(urlId, user)).thenReturn(Optional.of(existingUrl));
        when(urlRepository.existsByShortCode("xyz789")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Url result = urlService.updateUrl(urlId, updatedData, user);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("http://new.com", result.getOriginalUrl(), "Original URL should be updated");
        assertEquals("xyz789", result.getShortCode(), "Short code should be updated");
        assertEquals(urlId, result.getId(), "Id should be updated");

        verify(urlRepository, times(1)).findByIdAndUser(urlId, user);
        verify(urlRepository, times(1)).existsByShortCode("xyz789");
        verify(urlRepository, times(1)).save(existingUrl);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should throw RuntimeException when URL not found or unauthorized")
    void testUpdateUrl_NotFoundOrUnauthorized() {
        // Arrange
        Long urlId = 1L;
        User user = User.builder().id(1L).email("test@example.com").build();

        Url updatedData = new Url();
        updatedData.setOriginalUrl("http://new.com");
        updatedData.setShortCode("xyz789");

        when(urlRepository.findByIdAndUser(urlId, user)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            urlService.updateUrl(urlId, updatedData, user);
        });

        assertEquals("URL not found or user not authorized", exception.getMessage(), "Exception message should match");
        verify(urlRepository, times(1)).findByIdAndUser(urlId, user);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    @DisplayName("Should find URL by ID and User when present")
    void testFindByIdAndUser_Success() {
        // Arrange
        Long urlId = 1L;
        User user = User.builder().id(1L).email("test@example.com").build();

        Url mockUrl = new Url();
        mockUrl.setId(urlId);
        mockUrl.setUser(user);

        when(urlRepository.findByIdAndUser(eq(urlId), eq(user))).thenReturn(Optional.of(mockUrl));

        // Act
        Url result = urlService.findByIdAndUser(urlId, user);

        // Assert
        assertEquals(mockUrl, result, "The returned URL should match the mock URL");
    }

    @Test
    @DisplayName("Should throw ShortUrlException when URL not found")
    void testFindByIdAndUser_NotFound() {
        // Arrange
        Long urlId = 1L;
        User nonExistantUser = User.builder().id(1L).email("testNonExistantUser@example.com").build();

        when(urlRepository.findByIdAndUser(eq(urlId), eq(nonExistantUser))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShortUrlException.class, () -> urlService.findByIdAndUser(urlId, nonExistantUser), "Should throw ShortUrlException when URL is not found");
    }

    @Test
    @DisplayName("Should throw ShortUrlException when shortCode already exists")
    void testCreateUrl_ShortCodeAlreadyExists() {
        // Arrange
        User mockUser = User.builder().id(1L).email("test@example.com").build();

        UrlCreateRequest request = new UrlCreateRequest("https://example.com", "customCode");

        when(urlRepository.existsByShortCode(eq("customCode"))).thenReturn(true);

        // Act & Assert
        assertThrows(ShortUrlException.class, () -> urlService.createUrl(request, mockUser),
                "Should throw ShortUrlException when shortCode already exists");
    }
}
