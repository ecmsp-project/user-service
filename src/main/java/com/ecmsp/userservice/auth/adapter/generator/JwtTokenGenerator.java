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
import java.util.Set;
import java.util.stream.Collectors;
import com.ecmsp.common.userservice.user.domain.Permission;

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

        // Collect all permissions from all user roles
        Set<String> permissions = user.roles().stream()
                .flatMap(role -> role.permissions().stream())
                .map(Permission::name)
                .collect(Collectors.toSet());

        String jwt = Jwts.builder()
                .subject(user.id().value().toString())
                .claim("login", user.login())
                .claim("permissions", permissions)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(privateKey)
                .compact();

        return new Token(jwt);
    }
}
