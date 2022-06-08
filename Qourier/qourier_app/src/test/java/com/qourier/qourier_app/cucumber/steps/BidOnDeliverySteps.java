package com.qourier.qourier_app.cucumber.steps;

import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.*;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static com.qourier.qourier_app.TestUtils.createSampleRider;
import static org.assertj.core.api.Assertions.assertThat;

public class BidOnDeliverySteps {
    private final WebDriver driver = new FirefoxDriver();

    private final DeliveriesManager deliveriesManager;

    public BidOnDeliverySteps(DeliveriesManager deliveriesManager) {
        this.deliveriesManager = deliveriesManager;
    }

    @Given("a delivery was already created")
    public void deliveryAlreadyCreated() {
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");
        deliveriesManager.createDelivery(delivery);
        deliveriesManager.setNewAuctionSpan(10);
    }

    @And("I go to the {string} section")
    public void iGoToTheSection(String section) {
        driver.findElement(By.linkText(section)).click();
    }

    @When("I go to the page {string}")
    public void iGoToThePage(String url) {
        driver.get(url);
        driver.manage().window().setSize(new Dimension(1916, 1076));
    }

    @And("I click the check button on the line of the first delivery presented")
    public void iClickTheCheckButtonOnTheLineOfTheFirstDeliveryPresented() {
        driver.findElement(By.id("btn-delivery-2")).click();
    }

    @And("I click confirm")
    public void iClickConfirm() {
        driver.findElement(By.id("modal-btn-confirm")).click();
    }

    @And("I wait {int} seconds for the auction to end")
    public void iWaitSecondsForTheAuctionToEnd(int secondsToWait) {
        try {
            Thread.sleep(secondsToWait * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("My id should be assigned to the delivery")
    public void myIdShouldBeAssignedToTheDelivery() {
        assertThat(driver.getPageSource()).contains("rider_example@mial.com");
        driver.quit();
    }
}
