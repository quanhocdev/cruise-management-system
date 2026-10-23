package com.project.auth.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Configuration
public class RegisteredClientConfig {

        @Bean
        public RegisteredClientRepository registeredClientRepository(
                        PasswordEncoder passwordEncoder) {

                RegisteredClient bookingService = RegisteredClient.withId(
                                UUID.randomUUID().toString())
                                .clientId("booking-service")
                                .clientSecret(passwordEncoder.encode("booking-service-secret"))
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                .scope("tour.read")
                                .scope("tour.write")
                                .tokenSettings(TokenSettings.builder()
                                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                                .build())
                                .clientSettings(ClientSettings.builder()
                                                .requireAuthorizationConsent(false)
                                                .build())
                                .build();

                RegisteredClient tourService = RegisteredClient.withId(
                                UUID.randomUUID().toString())
                                .clientId("tour-service")
                                .clientSecret(passwordEncoder.encode("tour-service-secret"))
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                .scope("staff.read")
                                .tokenSettings(TokenSettings.builder()
                                                .accessTokenTimeToLive(Duration.ofMinutes(10))
                                                .build())
                                .clientSettings(ClientSettings.builder()
                                                .requireAuthorizationConsent(false)
                                                .build())
                                .build();

                return new InMemoryRegisteredClientRepository(
                                bookingService,
                                tourService);
        }
}