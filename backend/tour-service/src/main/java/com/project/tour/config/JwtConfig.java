package com.project.tour.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    private final String jwtSecret;

    public JwtConfig(
        @Value("${jwt.secret}")
        String jwtSecret
    ) {
        this.jwtSecret = jwtSecret;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(
            jwtSecret.getBytes(StandardCharsets.UTF_8),
            MacAlgorithm.HS256.getName()
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

        JwtClaimValidator<String> accessTokenValidator =
            new JwtClaimValidator<>(
                "tokenType",
                "ACCESS"::equals
            );

        decoder.setJwtValidator(
            new DelegatingOAuth2TokenValidator<Jwt>(
                JwtValidators.createDefault(),
                accessTokenValidator
            )
        );

        return decoder;
    }
}