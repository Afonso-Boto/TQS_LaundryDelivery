package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.cucumber.webpages.LoginPage;
import com.qourier.qourier_app.cucumber.webpages.NullPage;
import com.qourier.qourier_app.cucumber.webpages.WebPage;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class CucumberSteps {

    private final WebDriver driver = new HtmlUnitDriver(true);

    private WebPage currentPage;

    @Given("I am in the {page} page")
    public void IAmInPage(WebPage page) {
        currentPage = page;
    }

    @Given("I am logged in as a {accountRole}")
    public void loggedInAs(AccountRole accountRole) {

    }

    @Given("my application has been refused")
    public void applicationRefused() {

    }

    @Given("I have already been accepted to the platform")
    public void applicationHasBeenAccepted() {

    }

    @Given("my account is {not}suspended")
    public void accountSuspendedOrNot(boolean not) {

    }

    @When("I go to register myself as a {accountRole}")
    public void registerRider(AccountRole accountRole) {
        // TODO
    }

    @When("I fill the {accountRole} registration details")
    public void registerRiderDetails(AccountRole accountRole) {
        // TODO
    }

    @When("I register as a {accountRole}")
    public void registerRiderSubmit(AccountRole accountRole) {
        // TODO
    }

    @When("I go to the {section} section")
    public void goToSection(String section) {

    }

    @Then("my status is {accountState}")
    public void statusIs(AccountState accountState) {

    }

    @Then("the details are the same as the ones in the registration form")
    public void profileDetailsSameAsRegistration() {

    }

    @Then("there are {not}statistics")
    public void statisticsOrNot(boolean not) {

    }

    @ParameterType("\\w+")
    public WebPage page(String pageName) {
        return switch (pageName) {
            case "Login" -> new LoginPage(driver, 8080);
            default -> new NullPage();
        };
    }

    @ParameterType("'(pending|refused|active|suspended)'")
    public AccountState accountState(String accountStateStr) {
        return AccountState.valueOf(accountStateStr.toUpperCase());
    }

    @ParameterType("Rider|Customer")
    public AccountRole accountRole(String accountRoleStr) {
        return AccountRole.valueOf(accountRoleStr.toUpperCase());
    }

    @ParameterType("Profile")
    public String section(String section) {
        return section;
    }

    @ParameterType("not |")
    public boolean not(String not) {
        return !not.isEmpty();
    }

}
