package com.merchant.wallet.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gateway.routes[0].id=test",
    "spring.cloud.gateway.routes[0].uri=http://localhost:8080",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/test/**"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // Test that Spring context loads successfully
    }
}