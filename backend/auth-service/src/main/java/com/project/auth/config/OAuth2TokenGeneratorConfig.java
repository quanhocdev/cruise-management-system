package com.project.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

@Configuration
public class OAuth2TokenGeneratorConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingCustomizer() {

        return context -> {
            context.getJwsHeader()
                    .algorithm(MacAlgorithm.HS256);
        };
    }

    @Bean
    public org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator<?> tokenGenerator(
            JwtEncoder jwtEncoder,
            OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingCustomizer) {

        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);

        jwtGenerator.setJwtCustomizer(jwtEncodingCustomizer);

        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();

        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();

        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator,
                accessTokenGenerator,
                refreshTokenGenerator);
    }
}