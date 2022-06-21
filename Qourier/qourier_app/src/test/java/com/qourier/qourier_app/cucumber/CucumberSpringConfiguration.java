package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.message.MessageCenter;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("/application-test.properties")
@ExtendWith(MockitoExtension.class)
public class CucumberSpringConfiguration {

    @SpyBean
    private MessageCenter messageCenter;
}
