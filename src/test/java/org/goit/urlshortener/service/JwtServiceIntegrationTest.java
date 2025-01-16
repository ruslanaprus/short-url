package org.goit.urlshortener.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
class JwtServiceIntegrationTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        jwtService = new JwtService();

        var jwtSecretKeyField = JwtService.class.getDeclaredField("jwtSecretKey");
        jwtSecretKeyField.setAccessible(true);
        jwtSecretKeyField.set(jwtService, "ThisIsASecretKeyAndItIsSoSecureAndLongEnoughToUseItAsAKeyBecauseItContains32CharactersOrMore");

        var jwtExpirationField = JwtService.class.getDeclaredField("jwtExpirationMs");
        jwtExpirationField.setAccessible(true);
        jwtExpirationField.set(jwtService, 3600000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserDetails userDetails = Mockito.mock(User.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token, "Token should not be null");

        String extractedUsername = jwtService.extractUserName(token);
        assertEquals("testUser", extractedUsername, "Extracted username should match the expected value");

        assertTrue(jwtService.isTokenValid(token, userDetails), "Token should be valid");
        assertFalse(jwtService.isTokenExpired(token), "Token should not be expired");
    }

    @Test
    void testTokenExpiration() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        UserDetails userDetails = Mockito.mock(User.class);
        Mockito.when(userDetails.getUsername()).thenReturn("testUser");
        var jwtExpirationMsField = JwtService.class.getDeclaredField("jwtExpirationMs");
        jwtExpirationMsField.setAccessible(true);
        jwtExpirationMsField.set(jwtService, 1000L);

        String token = jwtService.generateToken(userDetails);

       Thread.sleep(2200L);
        try {
            jwtService.isTokenValid(token, userDetails);
            fail("Expected ExpiredJwtException to be thrown, but it wasn't");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            assertTrue(e.getMessage().contains("expired"), "Token should be expired");
        }
    }

    @Test
    void testExtractAllClaims() {
        SecretKey secretKey = Keys.hmacShaKeyFor(
                "ThisIsASecretKeyAndItIsSoSecureAndLongEnoughToUseItAsAKeyBecauseItContains32CharactersOrMore"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3600000L)) // 1 час
                .signWith(secretKey)
                .compact();

        assertEquals("testUser", jwtService.extractUserName(token), "Extracted username should match");
    }
}