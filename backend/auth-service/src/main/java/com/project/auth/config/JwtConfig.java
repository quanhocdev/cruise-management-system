package com.project.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import java.time.Duration;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(
                jwtSecret.getBytes(),
                MacAlgorithm.HS256.getName()
        );
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        return new ImmutableSecret<>(getSecretKey());
    }

    // Ký mã hóa JWT HS256
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }


    // Giải mã JWT và xác thực thời gian hết hạn
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Cài Clock Skew = 0
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ZERO);
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(timestampValidator));

        return jwtDecoder;
    }    
}