package com.qourier.qourier_app.cucumber;

import static com.qourier.qourier_app.TestUtils.SampleAccountBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import com.qourier.qourier_app.TestUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class CucumberSteps {

    private final WebDriver driver;
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final Rider sampleRider;
    private final Customer sampleCustomer;

    private final DeliveriesManager deliveriesManager;
    private AccountRole currentRole;

    public CucumberSteps(
            RiderRepository riderRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository,
            AccountRepository accountRepository,
            DeliveriesManager deliveriesManager) {
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;
        this.deliveriesManager = deliveriesManager;

        sampleRider = new SampleAccountBuilder("riderino@gmail.com").buildRider();
        sampleCustomer = new SampleAccountBuilder("customerino@gmail.com").buildCustomer();
        deliveriesManager.setNewAuctionSpan(2);

        driver = new HtmlUnitDriver(true);
    }

    @Given("I am in the {page} page")
    public void iAmInPage(String page) {
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
        iAmInPage("");
        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, account.getEmail()));
        iAmInPage("");
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

    @Given("the following accounts exist:")
    public void initializeAccounts(List<Map<String, String>> dataTable) {
        for (Map<String, String> accountDetails : dataTable) {
            String email = accountDetails.get("email");
            if (accountRepository.existsById(email)) continue;

            AccountRole role = AccountRole.valueOf(accountDetails.get("role").toUpperCase());
            AccountState state = AccountState.valueOf(accountDetails.get("state").toUpperCase());

            SampleAccountBuilder accountBuilder = new SampleAccountBuilder(email).state(state);
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

    @Given("a delivery was already created")
    public void deliveryAlreadyCreated() {
        Delivery delivery =
                new Delivery(
                        "test0@email.com", 99.99, 99.99, "test address", "test origin address");
        deliveriesManager.createDelivery(delivery);
        deliveriesManager.setNewAuctionSpan(10);
    }



    @When("I go to the {section} section")
    public void goToSection(String section) {
        driver.findElement(By.linkText(section)).click();
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

    @When("I filter for {accountsFilterType} accounts")
    public void filterActive(AccountState state) {
        WebElement activeFilter = driver.findElement(By.id("filter-active"));
        if (
                (state == AccountState.ACTIVE && !activeFilter.isSelected())
                        || (state == AccountState.SUSPENDED && activeFilter.isSelected())) {
            activeFilter.click();
        }
    }

    @When("I filter for {applicationsFilterType} applications")
    public void filterPending(AccountState state) {
        WebElement pendingFilter = driver.findElement(By.id("filter-pending"));
        if (
                (state == AccountState.PENDING && !pendingFilter.isSelected())
                        || (state == AccountState.REFUSED && pendingFilter.isSelected())) {
            pendingFilter.click();
        }
    }

    @When("I filter for {accountRole} accounts/applications")
    public void filterRole(AccountRole role) {
        WebElement roleDropdown = driver.findElement(By.id("filter-type"));
        String optionLabel = role.name().charAt(0) + role.name().substring(1).toLowerCase();
        roleDropdown.findElement(By.xpath("//option[. = '" + optionLabel + "']")).click();
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

    @When("I open the {string} application")
    public void openApplication(String email) {
        WebElement applicationButton = driver.findElement(By.id("btn-form-rider-" + TestUtils.hasher(email)));
        applicationButton.click();
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

    @When("I {applicationAction} their application")
    public void applicationApplyAction(String action) {
        WebElement actionForm = driver.findElement(By.id("rider-form-link-" + action));
        if (!actionForm.isDisplayed())
            actionForm = driver.findElement(By.id("customer-form-link-" + action));

        actionForm.findElement(By.tagName("button")).click();
    }

    @When("I go to check {endpoint} status")
    public void iGoToCheckDeliveriesStatus(String endpoint) {
        startOn("api/v1/deliveries");
    }

    @When("I navigate to {string}")
    public void iNavigateTo(String url) {
        driver.get(url);
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

    @Then("My id should be assigned to the delivery")
    public void myIdShouldBeAssignedToTheDelivery() {
        assertThat(driver.getPageSource()).contains("riderino@gmail.com");
        driver.quit();
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

    @Then("the status of {string} is {accountState}")
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



    @And("I wait {int} seconds for the auction to end")
    public void iWaitSecondsForTheAuctionToEnd(int secondsToWait) {
        try {
            // TODO: use awaitility
            Thread.sleep(secondsToWait * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @And("I click the check button on the line of the first delivery presented")
    public void iClickTheCheckButtonOnTheLineOfTheFirstDeliveryPresented() {
        driver.findElement(By.id("btn-delivery-1")).click();
    }

    @And("I click confirm")
    public void iClickConfirm() {
        driver.findElement(By.id("modal-btn-confirm")).click();
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

    private void startOn(String pagePath) {
        driver.get("http://localhost:8080/" + pagePath);
        driver.manage().window().setSize(new Dimension(1916, 1076));
    }
}
