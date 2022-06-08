package com.qourier.qourier_app.cucumber.steps;

import static com.qourier.qourier_app.TestUtils.createSampleRider;
import static com.qourier.qourier_app.TestUtils.createSampleCustomer;
import static org.assertj.core.api.Assertions.assertThat;

import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.*;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ProfileStatsSteps {

    private final WebDriver driver = new HtmlUnitDriver(true);
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final Rider sampleRider;
    private final Customer sampleCustomer;

    public ProfileStatsSteps(
            RiderRepository riderRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository,
            AccountRepository accountRepository) {
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;

        sampleRider = createSampleRider("riderino@gmail.com");
        sampleCustomer = createSampleCustomer("customerino@gmail.com");
    }

    private AccountRole currentRole;

    @Given("I am in the {page} page")
    public void IAmInPage(String page) {
        startOn(page);
    }

    @Given("I am logged in as a {accountRole}")
    public void loggedInAs(AccountRole accountRole) {
        Account account;
        if (accountRole.equals(AccountRole.RIDER)) {
            account = sampleRider.getAccount();
            currentRole = account.getRole();
            if (riderRepository.existsById(account.getEmail()))
                riderRepository.deleteById(account.getEmail());
            riderRepository.save(sampleRider);
        } else if (accountRole.equals(AccountRole.CUSTOMER)) {
            account = sampleCustomer.getAccount();
            currentRole = account.getRole();
            if (customerRepository.existsById(account.getEmail()))
                customerRepository.deleteById(account.getEmail());
            customerRepository.save(sampleCustomer);
        } else return;

        // Two calls have to be made because the document has to be initialized before a cookie can
        // be set
        IAmInPage("");
        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, account.getEmail()));
        IAmInPage("");
    }

    @Given("my application has been refused")
    public void applicationRefused() {
        if (currentRole.equals(AccountRole.RIDER)) {
            sampleRider.getAccount().setState(AccountState.REFUSED);
            riderRepository.save(sampleRider);
        } else if (currentRole.equals(AccountRole.CUSTOMER)) {
            sampleCustomer.getAccount().setState(AccountState.REFUSED);
            customerRepository.save(sampleCustomer);
        }
    }

    @Given("I have already been accepted to the platform")
    public void applicationHasBeenAccepted() {
        if (currentRole.equals(AccountRole.RIDER)) {
            sampleRider.getAccount().setState(AccountState.ACTIVE);
            riderRepository.save(sampleRider);
        } else if (currentRole.equals(AccountRole.CUSTOMER)) {
            sampleCustomer.getAccount().setState(AccountState.ACTIVE);
            customerRepository.save(sampleCustomer);
        }
    }

    @Given("my account is {not}suspended")
    public void accountSuspendedOrNot(boolean not) {
        if (currentRole.equals(AccountRole.RIDER)) {
            if (not) sampleRider.getAccount().setState(AccountState.ACTIVE);
            else sampleRider.getAccount().setState(AccountState.SUSPENDED);
            riderRepository.save(sampleRider);
        } else if (currentRole.equals(AccountRole.CUSTOMER)) {
            if (not) sampleCustomer.getAccount().setState(AccountState.ACTIVE);
            else sampleCustomer.getAccount().setState(AccountState.SUSPENDED);
            customerRepository.save(sampleCustomer);
        }
    }

    @When("I go to register myself as a(n) {accountRole}")
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
        } else if (currentRole.equals(AccountRole.CUSTOMER)) {
            driver.findElement(By.id("email")).sendKeys("customer_example@mial.com");
            driver.findElement(By.id("password")).sendKeys("the_password");
            driver.findElement(By.id("name")).sendKeys("Christina Laundry");
            driver.findElement(By.id("service_type")).sendKeys("Laundry");
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
            assertThat(driver.findElement(By.id("details-email")).getText())
                    .isEqualTo("rider_example@mial.com");
            assertThat(driver.findElement(By.id("details-citizen-id")).getText())
                    .isEqualTo("9901294");
            assertThat(driver.findElement(By.id("details-name")).getText()).isEqualTo("Diegos");
            assertThat(driver.findElement(By.id("account-type")).getText()).isEqualTo("Rider");
        } else if (currentRole.equals(AccountRole.CUSTOMER)) {
            assertThat(driver.findElement(By.id("details-email")).getText())
                    .isEqualTo("customer_example@mial.com");
            assertThat(driver.findElement(By.id("details-service-type")).getText())
                    .isEqualTo("Laundry");
            assertThat(driver.findElement(By.id("details-name")).getText())
                    .isEqualTo("Christina Laundry");
            assertThat(driver.findElement(By.id("account-type")).getText()).isEqualTo("Customer");
        }
    }

    @Then("there are {not}statistics")
    public void statisticsOrNot(boolean not) {
        List<List<WebElement>> statsElements = new ArrayList<>();

        statsElements.add(driver.findElements(By.id("statistics-section")));
        if (currentRole == AccountRole.RIDER) {
            statsElements.add(driver.findElements(By.id("statistics-deliveries-over-time")));
            statsElements.add(driver.findElements(By.id("statistics-average-time-spent")));
        } else if (currentRole == AccountRole.CUSTOMER) {
            statsElements.add(driver.findElements(By.id("statistics-delivery-request-rate")));
            statsElements.add(driver.findElements(By.id("statistics-delivery-average-time")));
        }

        if (not) assertThat(statsElements).allMatch(List::isEmpty);
        else assertThat(statsElements).noneMatch(List::isEmpty);
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

    private void startOn(String pagePath) {
        driver.get("http://localhost:8080/" + pagePath);
        driver.manage().window().setSize(new Dimension(1916, 1076));
    }
}
