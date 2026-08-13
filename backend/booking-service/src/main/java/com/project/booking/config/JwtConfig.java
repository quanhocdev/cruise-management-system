package com.project.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    @Bean JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(
            new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), MacAlgorithm.HS256.getName()))
            .macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(),
            new JwtClaimValidator<String>("tokenType", "ACCESS"::equals)));
        return decoder;
    }
}
