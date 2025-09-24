package com.ecmsp.userservice.auth.adapter.generator;

import com.ecmsp.userservice.auth.domain.Token;
import com.ecmsp.userservice.auth.domain.TokenGenerator;
import com.ecmsp.userservice.user.domain.User;
import io.jsonwebtoken.Jwts;

import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class JwtTokenGenerator implements TokenGenerator {
    private final PrivateKey privateKey;
    private final Clock clock;


    public JwtTokenGenerator(PrivateKey privateKey, Clock clock) {
        this.privateKey = privateKey;
        this.clock = clock;
    }

    @Override
    public Token generate(User user) {
        Instant now = clock.instant();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        String jwt = Jwts.builder()
                .subject(user.id().value().toString())
                .claim("login", user.login())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(privateKey)
                .compact();

        return new Token(jwt);
    }
}
