package org.goit.urlshortener.controller;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
@Import(RedirectControllerTest.TestConfig.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(urlRepository);
    }

    @Test
    void testSimpleRedirect() throws Exception {
        mockMvc.perform(get("/simple"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("https://example.com"));
    }

    @Test
    void testRedirectToOriginalUrl_Found() throws Exception {
        String shortCode = "shortCode1";
        String originalUrl = "https://original.com";

        Url url = Url.builder()
                .shortCode(shortCode)
                .originalUrl(originalUrl)
                .build();

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(url));

        mockMvc.perform(get("/s/" + shortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl(originalUrl));
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() throws Exception {
        String shortCode = "nonexistent";
        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/s/" + shortCode))
                .andExpect(status().isNotFound())
                .andExpect(redirectedUrl("/error"));
    }

    static class TestConfig {
        @Bean
        public UrlRepository urlRepository() {
            return mock(UrlRepository.class);
        }
    }
}