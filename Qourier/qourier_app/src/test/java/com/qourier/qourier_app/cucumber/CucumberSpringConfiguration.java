package com.qourier.qourier_app.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class CucumberSpringConfiguration {}
