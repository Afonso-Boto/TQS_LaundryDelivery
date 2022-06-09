package com.qourier.qourier_app.cucumber.steps;

import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GeneralSteps {

    private final WebDriver driver;

    public GeneralSteps() {
        driver = WebDriverHolder.getDriver();
    }

    @When("I go to the {section} section")
    public void goToSection(String section) {
        driver.findElement(By.linkText(section)).click();
    }
}
