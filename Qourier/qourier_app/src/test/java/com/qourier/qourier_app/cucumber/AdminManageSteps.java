package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.qourier.qourier_app.TestUtils.SampleAccountBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class AdminManageSteps {

    private final WebDriver driver = new HtmlUnitDriver(true);
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    public AdminManageSteps(
            RiderRepository riderRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository,
            AccountRepository accountRepository) {
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;
    }

    @Given("the following accounts exist:")
    public void initializeAccounts(List<Map<String, String>> dataTable) {
        for (Map<String, String> accountDetails : dataTable) {
            AccountRole role = AccountRole.valueOf(accountDetails.get("role").toUpperCase());
            String email = accountDetails.get("email");
            AccountState state = AccountState.valueOf(accountDetails.get("state").toUpperCase());

            SampleAccountBuilder accountBuilder = new SampleAccountBuilder(role, email).state(state);
            switch (role) {
                case RIDER -> riderRepository.save(accountBuilder.buildRider());
                case CUSTOMER -> customerRepository.save(accountBuilder.buildCustomer());
                case ADMIN -> adminRepository.save(accountBuilder.buildAdmin());
            }
        }
    }

    @Given("I am logged in as {string}")
    public void loggedInAs(String email) {
        Account account = accountRepository.findById(email)
                .orElseThrow();

        // Two calls have to be made because the document has to be initialized before a cookie can
        // be set
        driver.get("http://localhost:8080/");
        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, account.getEmail()));
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1916, 1076));
    }



    @When("I filter for {accountsFilterType} accounts")
    public void filterActive(AccountState state) {
        WebElement activeLabel = driver.findElement(By.cssSelector("label[for='filter-active']"));
        WebElement activeFilter = driver.findElement(By.id("filter-active"));
        if (
                (state.equals(AccountState.ACTIVE) && !activeFilter.isSelected())
                || (state.equals(AccountState.SUSPENDED) && activeFilter.isSelected())) {
            driver.findElement(By.id("filter-active")).click();
        }
    }

    @When("I apply the filters")
    public void applyFilters() {
        driver.findElement(By.id("filter-apply")).click();
    }

    @When("I go to the {string} profile")
    public void goToProfile(String email) {
        List<WebElement> tableRows = driver.findElements(By.cssSelector("tbody > tr"));
        for (WebElement row : tableRows) {
            WebElement idCell = row.findElement(By.cssSelector("td:first-child"));
            if (idCell.getText().equals(email)) {
                row.findElement(By.cssSelector("a")).click();
                break;
            }
        }
    }

    @When("I {accountAction} their account")
    public void accountApplyAction(String action) {
        WebElement toggleButton = driver.findElement(By.id("toggle-account"));
        if (action.equals("activate"))
            assertThat(toggleButton.getText()).startsWith("Activate");
        else
            assertThat(toggleButton.getText()).startsWith("Suspend");
        toggleButton.click();
    }

    @When("I go to the {section} section")
    public void goToSection(String section) {
        driver.findElement(By.linkText(section)).click();
    }



    @Then("the status of {string} is {accountsFilterType}")
    public void assertAccountState(String email, AccountState state) {
        String detailsState = driver.findElement(By.id("details-state")).getText();
        assertThat(AccountState.valueOf(detailsState.toUpperCase())).isEqualTo(state);

        Optional<Account> accountOptional = accountRepository.findById(email);
        assertThat(accountOptional).isPresent();
        Account account = accountOptional.get();
        assertThat(account.getState()).isEqualTo(state);
    }

    @Then("I can {accountAction} their account")
    public void accountCanAction(String action) {
        WebElement toggleButton = driver.findElement(By.id("toggle-account"));
        if (action.equals("activate"))
            assertThat(toggleButton.getText()).startsWith("Activate");
        else
            assertThat(toggleButton.getText()).startsWith("Suspend");
    }



    @ParameterType("\\w+")
    public String section(String section) {
        return section;
    }

    @ParameterType("not |")
    public boolean not(String not) {
        return !not.isEmpty();
    }

    @ParameterType("active|suspended")
    public AccountState accountsFilterType(String stateStr) {
        return AccountState.valueOf(stateStr.toUpperCase());
    }

    @ParameterType("activate|suspend")
    public String accountAction(String action) {
        return action;
    }
}
