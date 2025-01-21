package org.goit.urlshortener.url.controller;

import org.goit.urlshortener.url.model.Url;
import org.goit.urlshortener.auth.service.CustomUserDetailsService;
import org.goit.urlshortener.auth.service.JwtService;
import org.goit.urlshortener.url.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
@Import(RedirectControllerTest.TestConfig.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlService urlService;

    private Url testUrl;

    @BeforeEach
    void setUp() {
        testUrl = Url.builder()
                .id(1L)
                .shortCode("abc123")
                .originalUrl("https://example.com")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .clickCount(0L)
                .build();
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testRedirectToOriginalUrl_Success() throws Exception {
        when(urlService.getValidUrl("abc123")).thenReturn(testUrl);

        mockMvc.perform(get("/s/abc123"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("https://example.com"));

        verify(urlService).incrementClickCount(testUrl);
    }


    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testRedirectToOriginalUrl_InvalidCode() throws Exception {
        when(urlService.getValidUrl("invalidCode")).thenThrow(new RuntimeException("URL not found or invalid shortCode"));

        mockMvc.perform(get("/s/invalidCode"))
                .andExpect(status().isGone())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void testRedirectToOriginalUrl_ExpiredUrl() throws Exception {
        when(urlService.getValidUrl("expiredCode")).thenThrow(new RuntimeException("This URL has expired"));

        mockMvc.perform(get("/s/expiredCode"))
                .andExpect(status().isGone())
                .andExpect(redirectedUrl("/error"));
    }

    static class TestConfig {
        @Bean
        public UrlService urlService() {
            return mock(UrlService.class);
        }

        @Bean
        public JwtService jwtService() {
            return mock(JwtService.class);
        }

        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return mock(CustomUserDetailsService.class);
        }
    }
}