package com.ecmsp.userservice.auth.adapter.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(
        name = "auth.token-generator.type",
        havingValue = "jwt"
)
class JwtTokenGeneratorConfig {

    @Bean
    public JwtTokenGenerator jwtTokenGenerator(Clock clock, @Value("${auth.token-generator.secret-key-file}") File secretKeyFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyString = Files.readString(secretKeyFile.toPath()).trim();

        // Remove PEM headers and footers if present
        keyString = keyString.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                            .replaceAll("-----END PRIVATE KEY-----", "")
                            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        return new JwtTokenGenerator(privateKey, clock);
    }
}
