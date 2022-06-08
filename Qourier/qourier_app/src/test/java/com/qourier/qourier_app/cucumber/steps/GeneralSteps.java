package com.qourier.qourier_app.cucumber.steps;

import io.cucumber.java.en.When;
import org.openqa.selenium.By;

import static com.qourier.qourier_app.cucumber.steps.WebDriverHolder.driver;

public class GeneralSteps {

    @When("I go to the {section} section")
    public void goToSection(String section) {
        driver.findElement(By.linkText(section)).click();
    }

}
