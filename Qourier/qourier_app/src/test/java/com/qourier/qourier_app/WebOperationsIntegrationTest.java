package com.qourier.qourier_app;

import com.qourier.qourier_app.account.login.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations= "/application-test.properties")
class WebOperationsIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void givenAccountDoesNotExist_whenLoginIntoAccount_then() {
        LoginRequest request = new LoginRequest("goa", "ga");

        restTemplate.postForEntity("/login", request, ResponseBody.class);
    }

}
