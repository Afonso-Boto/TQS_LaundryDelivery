package com.qourier.qourier_app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class RegisterNonAdminSteps {

    private WebDriver driver;

    @When("I navigate to {string}")
    public void iNavigateTo(String url) {
        driver = new HtmlUnitDriver();
        driver.get(url);
    }

    @And("I click the register account button for the type Rider")
    public void iClickTheRegisterAccountButtonForTheType() {
        driver.findElement(By.cssSelector("div:nth-child(5) .btn")).click();
    }

    @And("I click the register account button for the type Customer")
    public void iClickTheRegisterAccountButtonForTheTypeCustomer() {
        driver.findElement(By.cssSelector("div:nth-child(7) .btn")).click();
    }

    @And("I set the Email as {string}")
    public void iSetTheEmailAs(String email) {
        driver.findElement(By.id("email")).sendKeys(email);
    }

    @And("I set the Password as {string}")
    public void iSetThePasswordAs(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @And("I set the Name as {string}")
    public void iSetTheNameAs(String name) {
        driver.findElement(By.id("name")).sendKeys(name);
    }

    @And("I set the Citizen ID as {string}")
    public void iSetTheCitizenIDAs(String CID) {
        driver.findElement(By.id("citizen_id")).sendKeys(CID);
    }

    @And("I set the Service Type as {string}")
    public void iSetTheServiceTypeAs(String servType) {
        driver.findElement(By.id("service_type")).sendKeys(servType);
    }

    @And("I click the register button")
    public void iClickRegister() {
        driver.findElement(By.id("register")).click();
    }

    @And("I click the {string} tab")
    public void iClickTheTab(String tab) {
        driver.findElement(By.linkText("Deliveries")).click();
    }

    @Then("I should see in the page body the pattern {string}")
    public void iShouldBeSeeInThePageBodyThePattern(String pattern) {
        try {
            assertThat(driver.findElement(By.id("non-permitted-message")).getText())
                    .isEqualTo(pattern);
        } catch (NoSuchElementException e) {
            throw new AssertionError("\"" + pattern + "\" not available in results");
        } finally {
            driver.quit();
        }
    }
}
