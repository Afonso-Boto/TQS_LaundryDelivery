package com.qourier.qourier_app.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com.qourier.qourier_app.cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.qourier.qourier_app.cucumber")
@Testcontainers
public class CucumberFunctionalTest {

    @Container
    public static MySQLContainer container = new MySQLContainer("mysql:8.0.29")
            .withUsername("demo")
            .withPassword("demopass")
            .withDatabaseName("test_db");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

}

