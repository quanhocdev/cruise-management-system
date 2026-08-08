// package com.project.gateway;

// import static org.assertj.core.api.Assertions.assertThat;

// import java.util.Set;
// import java.util.stream.Collectors;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
// import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

// @SpringBootTest(properties = {
//         "jwt.secret=01234567890123456789012345678901",
//         "JWT_SECRET=01234567890123456789012345678901"
// })
// class GatewayApplicationTests {

//     @Autowired
//     private RouteDefinitionLocator routeDefinitionLocator;

//     @Autowired
//     private ReactiveJwtDecoder jwtDecoder;

//     @Test
//     void contextLoadsWithJwtDecoder() {
//         assertThat(jwtDecoder).isNotNull();
//     }

//     @Test
//     void loadsAllConfiguredRoutes() {
//         Set<String> routeIds = routeDefinitionLocator.getRouteDefinitions()
//                 .map(route -> route.getId())
//                 .collect(Collectors.toSet())
//                 .block();

//         assertThat(routeIds).containsExactlyInAnyOrder(
//                 "auth-service",
//                 "booking-service",
//                 "activity-service",
//                 "convenience-service",
//                 "payment-service",
//                 "notification-service",
//                 "feedback-service",
//                 "tour-service");
//     }
// }
