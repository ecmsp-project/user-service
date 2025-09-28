package com.ecmsp.userservice.auth.adapter.generator;

import com.ecmsp.userservice.auth.domain.Token;
import com.ecmsp.userservice.user.domain.User;
import com.ecmsp.userservice.user.domain.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenGeneratorTest {

    private JwtTokenGenerator jwtTokenGenerator;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Clock fixedClock;
    private final Instant fixedInstant = Instant.now();

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        jwtTokenGenerator = new JwtTokenGenerator(privateKey, fixedClock);
    }
    @Test
    void shouldGenerateValidJwtToken() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        String login = "andy";
        User user = new User(new UserId(userId), login, "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        assertNotNull(token);
        assertNotNull(token.value());
        assertFalse(token.value().isEmpty());
    }

    @Test
    void shouldIncludeCorrectSubjectInToken() {
        UUID userId = UUID.randomUUID();
        String login = "testuser";
        User user = new User(new UserId(userId), login, "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        Claims claims = parseTokenClaims(token.value());
        assertEquals(userId.toString(), claims.getSubject());
    }

    @Test
    void shouldIncludeLoginClaimInToken() {
        UUID userId = UUID.randomUUID();
        String login = "testuser";
        User user = new User(new UserId(userId), login, "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        Claims claims = parseTokenClaims(token.value());
        assertEquals(login, claims.get("login"));
    }

    @Test
    void shouldSetCorrectIssuedAtTime() {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        Claims claims = parseTokenClaims(token.value());
        long expectedTime = fixedInstant.getEpochSecond();
        long actualTime = claims.getIssuedAt().toInstant().getEpochSecond();
        assertEquals(expectedTime, actualTime);
    }

    @Test
    void shouldSetExpirationTimeOneHourFromIssuedAt() {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        Claims claims = parseTokenClaims(token.value());
        long expectedExpirationTime = fixedInstant.plusSeconds(3600).getEpochSecond(); // 1 hour
        long actualExpirationTime = claims.getExpiration().toInstant().getEpochSecond();
        assertEquals(expectedExpirationTime, actualExpirationTime);
    }

    @Test
    void shouldSignTokenWithProvidedPrivateKey() {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        assertDoesNotThrow(() -> {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token.value());
        });
    }

    @Test
    void shouldThrowExceptionWhenVerifyingWithWrongKey() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair wrongKeyPair = keyPairGenerator.generateKeyPair();
        PublicKey wrongPublicKey = wrongKeyPair.getPublic();

        assertThrows(Exception.class, () -> Jwts.parser()
                .verifyWith(wrongPublicKey)
                .build()
                .parseSignedClaims(token.value()));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        User user1 = new User(new UserId(UUID.randomUUID()), "user1", "password1");
        User user2 = new User(new UserId(UUID.randomUUID()), "user2", "password2");

        Token token1 = jwtTokenGenerator.generate(user1);
        Token token2 = jwtTokenGenerator.generate(user2);

        assertNotEquals(token1.value(), token2.value());
    }

    @Test
    void shouldGenerateDifferentTokensAtDifferentTimes() {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token1 = jwtTokenGenerator.generate(user);

        Clock laterClock = Clock.fixed(fixedInstant.plusSeconds(60), ZoneOffset.UTC);
        JwtTokenGenerator laterGenerator = new JwtTokenGenerator(privateKey, laterClock);
        Token token2 = laterGenerator.generate(user);

        assertNotEquals(token1.value(), token2.value());
    }

    @Test
    void shouldGenerateCompactJwtFormat() {
        UUID userId = UUID.randomUUID();
        User user = new User(new UserId(userId), "testuser", "hashedPassword");

        Token token = jwtTokenGenerator.generate(user);

        String[] parts = token.value().split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts separated by dots");

        for (String part : parts) {
            assertFalse(part.isEmpty(), "Each JWT part should not be empty");
        }
    }

    private Claims parseTokenClaims(String token) {
        return Jwts.parser()
            .verifyWith(publicKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}