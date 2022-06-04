package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import com.qourier.qourier_app.data.Rider;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.qourier.qourier_app.TestUtils.createSampleRider;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
public class CucumberSteps {

//    private final WebDriver driver = new HtmlUnitDriver(true);
    private final WebDriver driver = WebDriverManager.firefoxdriver().create();
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final Rider sampleRider;

    public CucumberSteps(
            RiderRepository riderRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository,
            AccountRepository accountRepository) {
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;

        sampleRider = createSampleRider("riderino@gmail.com");
    }

    private AccountRole currentRole;
    private Account currentAccount;

    @Given("I am in the {page} page")
    public void IAmInPage(String page) {
        reset();
        startOn(page);
    }

    @Given("I am logged in as a {accountRole}")
    public void loggedInAs(AccountRole accountRole) {
        if (accountRole.equals(AccountRole.RIDER)) {
            riderRepository.save(sampleRider);
            currentAccount = sampleRider.getAccount();
        }
        else return;

        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, currentAccount.getEmail()));
        IAmInPage("Main");
    }

    @Given("my application has been refused")
    public void applicationRefused() {
        currentAccount.setState(AccountState.REFUSED);
        accountRepository.save(currentAccount);
    }

    @Given("I have already been accepted to the platform")
    public void applicationHasBeenAccepted() {
        currentAccount.setState(AccountState.ACTIVE);
        accountRepository.save(currentAccount);
    }

    @Given("my account is {not}suspended")
    public void accountSuspendedOrNot(boolean not) {
        if (not) currentAccount.setState(AccountState.ACTIVE);
        else currentAccount.setState(AccountState.SUSPENDED);
        accountRepository.save(currentAccount);
    }



    @When("I go to register myself as a {accountRole}")
    public void registerAs(AccountRole accountRole) {
        String role = accountRole.name().toLowerCase();
        driver.findElement(By.id("btn-register-" + role)).click();
        currentRole = accountRole;
    }

    @When("I fill the registration details")
    public void registerDetailsAs() {
        if (currentRole.equals(AccountRole.RIDER)) {
            driver.findElement(By.id("email")).sendKeys("rider_example@mial.com");
            driver.findElement(By.id("password")).sendKeys("secret");
            driver.findElement(By.id("name")).sendKeys("Diegos");
            driver.findElement(By.id("citizen_id")).sendKeys("9901294");
        }
    }

    @When("I register")
    public void registerRiderSubmit() {
        driver.findElement(By.id("register")).click();
    }

    @When("I go to the {section} section")
    public void goToSection(String section) {
        driver.findElement(By.linkText(section)).click();
    }



    @Then("my status is {accountState}")
    public void statusIs(AccountState accountState) {
        String state = accountState.name().toLowerCase();
        assertThat(driver.findElement(By.id("details-state")).getText()).isEqualTo(state);
    }

    @Then("the details are the same as the ones in the registration form")
    public void profileDetailsSameAsRegistration() {
        if (currentRole.equals(AccountRole.RIDER)) {
            assertThat(driver.findElement(By.id("details-email")).getText()).isEqualTo("rider_example@mial.com");
            assertThat(driver.findElement(By.id("details-citizen-id")).getText()).isEqualTo("9901294");
            assertThat(driver.findElement(By.id("details-name")).getText()).isEqualTo("Diego");
            assertThat(driver.findElement(By.id("account-type")).getText()).isEqualTo("Rider");
        }
    }

    @Then("there are {not}statistics")
    public void statisticsOrNot(boolean not) {
        List<WebElement> elements = driver.findElements(By.id("statistics-section"));
        if (not) assertThat(elements).isEmpty();
        else assertThat(elements).isNotEmpty();
    }



    @ParameterType("Login|Main")
    public String page(String pageName) {
        return (pageName.equals("Main")) ? "" : "login";
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



    private void reset() {
        riderRepository.deleteAll();
        customerRepository.deleteAll();
        adminRepository.deleteAll();
        accountRepository.deleteAll();
        driver.manage().deleteCookieNamed(WebController.COOKIE_ID);
    }

    private void startOn(String pagePath) {
        driver.get("http://localhost:8080/" + pagePath);
        driver.manage().window().setSize(new Dimension(1916, 1076));
    }

}
