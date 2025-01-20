package org.goit.urlshortener.controller;

import org.goit.urlshortener.TestcontainersConfiguration;
import org.goit.urlshortener.exceptionHandler.ExceptionMessages;
import org.goit.urlshortener.exceptionHandler.ShortUrlException;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.mapper.UrlMapper;
import org.goit.urlshortener.model.dto.request.UrlCreateRequest;
import org.goit.urlshortener.model.dto.response.UrlResponse;
import org.goit.urlshortener.repository.UserRepository;
import org.goit.urlshortener.service.url.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UrlMapper urlMapper;

    @Autowired
    private UserRepository userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRepo.deleteAll();
        testUser = new User("testUser@mail.com", "dummy_secret");
        testUser = userRepo.save(testUser);
        SecurityContextHolder.clearContext(); // clear any previous auth
    }

    @Test
    @DisplayName("GET /api/v1/urls - Should return a list of URLs for the user")
    void listUrlsReturnsUrls() throws Exception {
        // Mock user
        User mockUser = User.builder().id(1L).email("test@example.com").build();

        // Mock URLs
        List<Url> urls = List.of(
                Url.builder().id(1L).originalUrl("http://example.com").shortCode("url").build(),
                Url.builder().id(1L).originalUrl("http://example.com").shortCode("url").build()
        );
        Page<Url> urlPage = new PageImpl<>(urls);

        // Mock service and mapper
        when(urlService.listUrlsByStatus(eq(mockUser), eq("all"), any(PageRequest.class)))
                .thenReturn(urlPage);

        when(urlMapper.toUrlResponse(any(Url.class)))
                .thenAnswer(invocation -> {
                    Url url = invocation.getArgument(0);
                    return new UrlResponse(url.getOriginalUrl(), url.getShortCode(), url.getClickCount());
                });

        // Perform request
        mockMvc.perform(get("/api/v1/urls")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "all")
                        .with(user(mockUser))) // Simulate authenticated user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].originalUrl").value("http://example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/urls - Should create a new URL")
    void createUrl() throws Exception {
        // Given
        String originalUrl = "https://example.com";
        String shortCode = "short";

        UrlCreateRequest request = UrlCreateRequest.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .build();

        Url savedUrl = Url.builder()
                .id(1L)
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        UrlResponse expectedResponse = new UrlResponse(
                originalUrl,
                shortCode,
                0L
        );
        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // Mock the service and mapper behavior
        when(urlService.createUrl(eq(request), any(User.class)))
                .thenReturn(savedUrl);
        when(urlMapper.toUrlResponse(savedUrl))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"https://example.com\", \"shortCode\": \"short\"}"))
                .andDo(print())  // <= prints request and response in the test logs
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortCode").value("short"))
                .andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    @DisplayName("POST /api/v1/urls - Should return 400 if URL is invalid")
    void createUrl_invalidUrl() throws Exception {
        // Given
        String invalidUrl = "htp://invalid-url";

        UrlCreateRequest request = UrlCreateRequest.builder()
                .originalUrl(invalidUrl)
                .build();

        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // Mock the service to simulate invalid URL validation
        when(urlService.createUrl(eq(request), any(User.class)))
                .thenThrow(new ShortUrlException(ExceptionMessages.INVALID_ORIGINAL_URL_DATA));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"htp://invalid-url\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request data"));
    }

}