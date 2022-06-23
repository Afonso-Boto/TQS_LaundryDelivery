package com.qourier.qourier_app.cucumber;

import static com.qourier.qourier_app.TestUtils.SampleAccountBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.qourier.qourier_app.TestUtils;
import com.qourier.qourier_app.bids.DeliveriesManager;
import com.qourier.qourier_app.controller.ApiController;
import com.qourier.qourier_app.controller.WebController;
import com.qourier.qourier_app.data.*;
import com.qourier.qourier_app.message.MessageCenter;
import com.qourier.qourier_app.repository.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class CucumberSteps {

    private final WebDriver driver;
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final Rider sampleRider;
    private final Customer sampleCustomer;
    private final DeliveriesManager deliveriesManager;
    private final BidsRepository bidsRepository;
    private final DeliveryRepository deliveryRepository;
    private final ApiController apiController;
    private final int auctionSpan;

    @Autowired private MessageCenter messageCenter;

    private Account currentAccount;
    private long focusedDeliveryId;
    private AccountRole focusedRole;
    private String focusedAccountId;

    public CucumberSteps(
            RiderRepository riderRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository,
            AccountRepository accountRepository,
            DeliveriesManager deliveriesManager,
            BidsRepository bidsRepository,
            DeliveryRepository deliveryRepository,
            ApiController apiController) {
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;
        this.deliveriesManager = deliveriesManager;
        this.bidsRepository = bidsRepository;
        this.deliveryRepository = deliveryRepository;
        this.apiController = apiController;

        sampleRider = new SampleAccountBuilder("riderino@gmail.com").buildRider();
        sampleCustomer = new SampleAccountBuilder("customerino@gmail.com").buildCustomer();
        auctionSpan = 5;
        deliveriesManager.setNewAuctionSpan(auctionSpan);

        driver = new HtmlUnitDriver(true);
    }

    @Given("I am in the {page} page")
    public void iAmInPage(String page) {
        startOn(page);
    }

    @Given("I am logged in as a {accountRole}")
    public void loggedInAs(AccountRole accountRole) {
        if (accountRole.equals(AccountRole.RIDER)) {
            currentAccount = sampleRider.getAccount();
            if (riderRepository.existsById(currentAccount.getEmail()))
                riderRepository.deleteById(currentAccount.getEmail());
            riderRepository.save(sampleRider);
        } else if (accountRole.equals(AccountRole.CUSTOMER)) {
            currentAccount = sampleCustomer.getAccount();
            if (customerRepository.existsById(currentAccount.getEmail()))
                customerRepository.deleteById(currentAccount.getEmail());
            customerRepository.save(sampleCustomer);
        } else return;

        // Two calls have to be made because the document has to be initialized before a cookie can
        // be set
        iAmInPage("");
        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, currentAccount.getEmail()));
        iAmInPage("");
    }

    @Given("my application has been refused")
    public void applicationRefused() {
        if (currentAccount.getRole().equals(AccountRole.RIDER)) {
            sampleRider.getAccount().setState(AccountState.REFUSED);
            riderRepository.save(sampleRider);
        } else if (currentAccount.getRole().equals(AccountRole.CUSTOMER)) {
            sampleCustomer.getAccount().setState(AccountState.REFUSED);
            customerRepository.save(sampleCustomer);
        }
    }

    @Given("I have already been accepted to the platform")
    public void applicationHasBeenAccepted() {
        if (currentAccount.getRole().equals(AccountRole.RIDER)) {
            sampleRider.getAccount().setState(AccountState.ACTIVE);
            riderRepository.save(sampleRider);
        } else if (currentAccount.getRole().equals(AccountRole.CUSTOMER)) {
            sampleCustomer.getAccount().setState(AccountState.ACTIVE);
            customerRepository.save(sampleCustomer);
        }
    }

    @Given("my account is {not}suspended")
    public void accountSuspendedOrNot(boolean not) {
        if (currentAccount.getRole().equals(AccountRole.RIDER)) {
            if (not) sampleRider.getAccount().setState(AccountState.ACTIVE);
            else sampleRider.getAccount().setState(AccountState.SUSPENDED);
            riderRepository.save(sampleRider);
        } else if (currentAccount.getRole().equals(AccountRole.CUSTOMER)) {
            if (not) sampleCustomer.getAccount().setState(AccountState.ACTIVE);
            else sampleCustomer.getAccount().setState(AccountState.SUSPENDED);
            customerRepository.save(sampleCustomer);
        }
    }

    @Given("the following accounts exist:")
    public void initializeAccounts(List<Map<String, String>> dataTable) {
        for (Map<String, String> accountDetails : dataTable) {
            String email = accountDetails.get("email");
            if (accountRepository.existsById(email)) {
                Account account = accountRepository.findById(email).orElseThrow();

                switch (account.getRole()) {
                    case RIDER -> riderRepository.deleteById(email);
                    case CUSTOMER -> customerRepository.deleteById(email);
                    case ADMIN -> adminRepository.deleteById(email);
                }
            }

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

    @Given("the following deliveries are up:")
    public void initializeDeliveries(List<Map<String, String>> dataTable) {
        for (Map<String, String> deliveryDetails : dataTable) {
            String customerId = deliveryDetails.get("customer");
            double latitude = Double.parseDouble(deliveryDetails.get("latitude"));
            double longitude = Double.parseDouble(deliveryDetails.get("longitude"));
            Customer customer = customerRepository.findById(customerId).orElseThrow();

            Delivery delivery =
                    new Delivery(
                            customer.getEmail(),
                            latitude,
                            longitude,
                            TestUtils.randomString(),
                            TestUtils.randomString());

            if (deliveryDetails.containsKey("rider"))
                delivery.setRiderId(deliveryDetails.get("rider"));

            if (deliveryDetails.containsKey("state"))
                delivery.setDeliveryState(
                        DeliveryState.valueOf(
                                deliveryDetails.get("state").toUpperCase().replace(' ', '_')));

            if (delivery.getDeliveryState() == DeliveryState.BID_CHECK)
                deliveriesManager.createDelivery(delivery);
            else {
                deliveryRepository.save(delivery);
                if (delivery.getRiderId() != null) {
                    Rider assignedRider =
                            riderRepository.findById(delivery.getRiderId()).orElseThrow();
                    assignedRider.setCurrentDelivery(delivery.getDeliveryId());
                    riderRepository.save(assignedRider);
                }
            }

            focusedDeliveryId = delivery.getDeliveryId();
        }
    }

    @Given("the following bids have been done:")
    public void initializeBids(List<Map<String, String>> dataTable) {
        for (Map<String, String> bidDetails : dataTable) {
            String customerEmail = bidDetails.get("customer");
            double latitude = Double.parseDouble(bidDetails.get("latitude"));
            double longitude = Double.parseDouble(bidDetails.get("longitude"));
            String riderId = bidDetails.get("rider");
            double distance = Double.parseDouble(bidDetails.get("distance"));

            long deliveryId = getDeliveryId(customerEmail, latitude, longitude).getDeliveryId();
            deliveriesManager.createBid(new Bid(riderId, deliveryId, distance));
        }
    }

    @Given("I am logged in as {string}")
    public void loggedInAs(String email) {
        currentAccount = accountRepository.findById(email).orElseThrow();

        // Two calls have to be made because the document has to be initialized before a cookie can
        // be set
        driver.get("http://localhost:8080/");
        driver.manage().addCookie(new Cookie(WebController.COOKIE_ID, currentAccount.getEmail()));
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
        focusedRole = accountRole;
    }

    @When("I fill the registration details")
    public void registerDetailsAs() {
        if (focusedRole == AccountRole.RIDER) {
            driver.findElement(By.id("email")).sendKeys("rider_example@mial.com");
            driver.findElement(By.id("password")).sendKeys("secret");
            driver.findElement(By.id("name")).sendKeys("Diegos");
            driver.findElement(By.id("citizen_id")).sendKeys("9901294");
        } else if (focusedRole == AccountRole.CUSTOMER) {
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
        if ((state == AccountState.ACTIVE && !activeFilter.isSelected())
                || (state == AccountState.SUSPENDED && activeFilter.isSelected())) {
            activeFilter.click();
        }
    }

    @When("I filter for {applicationsFilterType} applications")
    public void filterPending(AccountState state) {
        WebElement pendingFilter = driver.findElement(By.id("filter-pending"));
        if ((state == AccountState.PENDING && !pendingFilter.isSelected())
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
        WebElement applicationButton;
        try {
            applicationButton =
                    driver.findElement(By.id("btn-form-rider-" + TestUtils.hasher(email)));
        } catch (NoSuchElementException ex) {
            applicationButton =
                    driver.findElement(By.id("btn-form-customer-" + TestUtils.hasher(email)));
        }
        applicationButton.click();
    }

    @When("I {accountAction} their account")
    public void accountApplyAction(String action) {
        WebElement toggleButton = driver.findElement(By.id("toggle-account"));
        if (action.equals("activate")) assertThat(toggleButton.getText()).startsWith("Activate");
        else assertThat(toggleButton.getText()).startsWith("Suspend");
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

    @When("I wait for the auction to end")
    public void waitAuctionEnd() {
        await().atMost(auctionSpan * 2L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> verify(messageCenter).notifyRiderAssignment(anyString(), anyLong()));
    }

    @When("I fill the delivery details")
    public void fillDeliveryDetails() {
        driver.findElement(By.id("form-delivery-origin")).sendKeys(TestUtils.randomString());
        driver.findElement(By.id("form-delivery-destination")).sendKeys(TestUtils.randomString());
        driver.findElement(By.id("form-delivery-latitude")).sendKeys("10");
        driver.findElement(By.id("form-delivery-longitude")).sendKeys("10");
    }

    @When("I register the delivery")
    public void registerDelivery() {
        WebElement registerDeliveryButton = driver.findElement(By.id("btn-register-delivery"));
        assertThat(registerDeliveryButton.isDisplayed()).isTrue();
        registerDeliveryButton.click();
        focusedDeliveryId =
                deliveriesManager.getAllDeliveries().stream()
                        .max(Comparator.comparing(Delivery::getCreationTime))
                        .orElseThrow()
                        .getDeliveryId();
    }

    @When("I indicate that I picked up the delivery")
    public void markDeliveryPickedUp() {
        driver.findElement(By.id("pickup-delivery")).click();
    }

    @When("I mark the delivery as being done")
    public void markDeliveryDone() {
        driver.findElement(By.id("confirm-delivery")).click();
    }

    @Then("a rider assignment notification should have been sent")
    public void riderAssignmentNotificationSent() {
        verify(messageCenter, times(1))
                .notifyRiderAssignment(currentAccount.getEmail(), focusedDeliveryId);
    }

    @Then("I should {not}receive a notification indicating that I have been accepted")
    public void iShouldReceiveDeliveryAcceptedNotification(boolean not) {
        await().atMost(5L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            WebElement alert = driver.findElement(By.id("info-delivery-assigned"));
                            if (not) assertThat(alert.isDisplayed()).isFalse();
                            else assertThat(alert.isDisplayed()).isTrue();
                        });
    }

    @Then("I should be presented with the delivery's details")
    public void iShouldBePresentedWithDeliveryDetails() {
        Delivery updatedDelivery = deliveriesManager.getDelivery(focusedDeliveryId);
        WebElement detailsCustomer = driver.findElement(By.id("assigned-delivery-customer"));
        WebElement detailsOrigin = driver.findElement(By.id("assigned-delivery-origin"));
        WebElement detailsDestination = driver.findElement(By.id("assigned-delivery-destination"));
        WebElement detailsState = driver.findElement(By.id("assigned-delivery-state"));

        assertThat(detailsCustomer.getText()).isEqualTo(updatedDelivery.getCustomerId());
        assertThat(detailsOrigin.getText()).isEqualTo(updatedDelivery.getOriginAddr());
        assertThat(detailsDestination.getText()).isEqualTo(updatedDelivery.getDeliveryAddr());
        assertThat(detailsState.getText()).isEqualTo(updatedDelivery.getDeliveryState().toString());
    }

    @Then("I should {not}be the assigned Rider for the delivery")
    public void iShouldBeDeliveryAssignedRider(boolean not) {
        Delivery updatedDelivery = deliveriesManager.getDelivery(focusedDeliveryId);
        if (not) assertThat(updatedDelivery.getRiderId()).isNotEqualTo(currentAccount.getEmail());
        else assertThat(updatedDelivery.getRiderId()).isEqualTo(currentAccount.getEmail());
    }

    @Then("I can {not}bid for another delivery")
    public void assertBidAbility(boolean not) {
        WebElement infoDeliveriesAvailable = driver.findElement(By.id("info-delivery-available"));
        WebElement infoDeliveryAlreadyAssigned =
                driver.findElement(By.id("info-delivery-assigned"));
        if (not) {
            assertThat(infoDeliveriesAvailable.isDisplayed()).isFalse();
            assertThat(infoDeliveryAlreadyAssigned.isDisplayed()).isTrue();
        } else {
            assertThat(infoDeliveriesAvailable.isDisplayed()).isTrue();
            assertThat(infoDeliveryAlreadyAssigned.isDisplayed()).isFalse();
        }
    }

    @Then("the delivery job is {not}up for bidding")
    public void assertDeliveryNotUpForBidding(boolean not) {
        DeliveryState state = deliveriesManager.getDelivery(focusedDeliveryId).getDeliveryState();
        if (not) assertThat(state).isNotEqualTo(DeliveryState.BID_CHECK);
        else assertThat(state).isEqualTo(DeliveryState.BID_CHECK);
    }

    @Then("the delivery job is registered as {deliveryStatus}")
    public void assertDeliveryJobDone(DeliveryState state) {
        Delivery delivery = deliveriesManager.getDelivery(focusedDeliveryId);
        assertThat(delivery.getDeliveryState()).isEqualTo(state);

        if (state == DeliveryState.FETCHING || state == DeliveryState.SHIPPED) {
            WebElement detailsState = driver.findElement(By.id("assigned-delivery-state"));
            assertThat(detailsState.isDisplayed()).isTrue();
            assertThat(detailsState.getText()).isEqualTo(state.toString());
        }
    }

    @Then("my status is {accountState}")
    public void statusIs(AccountState accountState) {
        String state = accountState.name().toLowerCase();
        assertThat(driver.findElement(By.id("details-state")).getText()).isEqualTo(state);
    }

    @Then("the details are the same as the ones in the registration form")
    public void profileDetailsSameAsRegistration() {
        if (focusedRole == AccountRole.RIDER) {
            assertThat(driver.findElement(By.id("details-email")).getText())
                    .isEqualTo("rider_example@mial.com");
            assertThat(driver.findElement(By.id("details-citizen-id")).getText())
                    .isEqualTo("9901294");
            assertThat(driver.findElement(By.id("details-name")).getText()).isEqualTo("Diegos");
            assertThat(driver.findElement(By.id("account-type")).getText()).isEqualTo("Rider");
        } else if (focusedRole == AccountRole.CUSTOMER) {
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
        if (focusedRole == AccountRole.RIDER) {
            statsElements.add(driver.findElements(By.id("statistics-deliveries-over-time")));
            statsElements.add(driver.findElements(By.id("statistics-average-time-spent")));
        } else if (focusedRole == AccountRole.CUSTOMER) {
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

    @Then("the status of {string} on the profile is {accountState}")
    public void assertAccountStateOnProfile(String email, AccountState state) {
        String detailsState = driver.findElement(By.id("details-state")).getText();
        assertThat(AccountState.valueOf(detailsState.toUpperCase())).isEqualTo(state);

        assertAccountState(email, state);
    }

    @Then("the status of {string} is {accountState}")
    public void assertAccountState(String email, AccountState state) {
        Optional<Account> accountOptional = accountRepository.findById(email);
        assertThat(accountOptional).isPresent();
        Account account = accountOptional.get();
        assertThat(account.getState()).isEqualTo(state);
    }

    @Then("I can {accountAction} their account")
    public void accountCanAction(String action) {
        WebElement toggleButton = driver.findElement(By.id("toggle-account"));
        if (action.equals("activate")) assertThat(toggleButton.getText()).startsWith("Activate");
        else assertThat(toggleButton.getText()).startsWith("Suspend");
    }

    @Then("the delivery registration form is empty")
    public void assertDeliveryRegistrationFormEmpty() {
        List<WebElement> formElements =
                Stream.of(
                                "form-delivery-origin",
                                "form-delivery-destination",
                                "form-delivery-latitude",
                                "form-delivery-longitude")
                        .map(id -> driver.findElement(By.id(id)))
                        .toList();

        for (WebElement formElement : formElements) {
            assertThat(formElement.isDisplayed()).isTrue();
            assertThat(formElement.getAttribute("value")).isEmpty();
        }
    }

    @Then("a table of the currently participating Riders' progress is shown")
    public void assertRiderProgressTable() {
        List<WebElement> tableRows =
                driver.findElements(By.cssSelector("#progress-table-body > tr"));
        assertThat(tableRows).hasSize(deliveriesManager.getAllDeliveries().size());
    }

    @Then("a table with all requested, in progress and completed deliveries is shown")
    public void assertDeliveryProgressTable() {
        List<WebElement> tableRows =
                driver.findElements(By.cssSelector("#delivery-table-body > tr"));
        assertThat(tableRows)
                .hasSize(
                        deliveriesManager
                                .getDeliveriesFromCustomer(currentAccount.getEmail())
                                .size());
    }

    @Then("the deliveries in progress contain a progress bar")
    public void assertDeliveryProgressShownIsReal() {
        List<WebElement> progressBars = driver.findElements(By.cssSelector(".progress > div"));
        assertThat(progressBars)
                .hasSizeGreaterThan(
                        (int)
                                deliveriesManager
                                        .getDeliveriesFromCustomer(currentAccount.getEmail())
                                        .stream()
                                        .filter(
                                                delivery ->
                                                        delivery.getDeliveryState()
                                                                != DeliveryState.DELIVERED)
                                        .count());

        for (WebElement progressBar : progressBars) {
            String[] progressBarIdSplit = progressBar.getAttribute("id").split("-");
            long deliveryId = Long.parseLong(progressBarIdSplit[progressBarIdSplit.length - 1]);
            String[] progressBarStyleSplit = progressBar.getAttribute("style").split(":");

            String progressBarWidth = null;
            for (int i = 0; i < progressBarStyleSplit.length; i++) {
                if (progressBarStyleSplit[i].equals("width")) {
                    assertThat(progressBarStyleSplit).hasSizeGreaterThan(i + 1);
                    progressBarWidth = progressBarStyleSplit[i + 1];
                    break;
                }
            }
            assertThat(progressBarWidth).isNotNull();

            Delivery delivery = deliveriesManager.getDelivery(deliveryId);
            DeliveryState presentedState =
                    switch (progressBarWidth) {
                        case "25%" -> DeliveryState.BID_CHECK;
                        case "50%" -> DeliveryState.FETCHING;
                        case "75%" -> DeliveryState.SHIPPED;
                        case "100%" -> DeliveryState.DELIVERED;
                        default -> null;
                    };

            assertThat(presentedState).isNotNull();
            assertThat(presentedState).isEqualTo(delivery.getDeliveryState());
        }
    }

    @Then("I can see the profile of {string} with all details inputted in their registration")
    public void validProfileShown(String profileEmail) {
        Account account = accountRepository.findById(profileEmail).orElseThrow();

        WebElement detailsEmail = driver.findElement(By.id("details-email"));
        assertThat(detailsEmail.getText()).isEqualTo(account.getEmail());
        WebElement detailsName = driver.findElement(By.id("details-name"));
        assertThat(detailsName.getText()).isEqualTo(account.getName());
        WebElement detailsRegistrationTime = driver.findElement(By.id("details-registration-time"));
        assertThat(detailsRegistrationTime.getText())
                .isEqualTo(account.getRegistrationTime().toString());

        switch (account.getRole()) {
            case RIDER -> {
                Rider rider = riderRepository.findById(account.getEmail()).orElseThrow();

                WebElement detailsCitizenId = driver.findElement(By.id("details-citizen-id"));
                assertThat(detailsCitizenId.getText()).isEqualTo(rider.getCitizenId());
            }
            case CUSTOMER -> {
                Customer customer = customerRepository.findById(account.getEmail()).orElseThrow();

                WebElement detailsServiceType = driver.findElement(By.id("details-service-type"));
                assertThat(detailsServiceType.getText()).isEqualTo(customer.getServType());
            }
        }

        focusedAccountId = profileEmail;
    }

    @Then("I can see statistics about the number of deliveries done")
    public void iCanSeeRiderStatistics() {
        WebElement statsNumberOfDeliveriesDone =
                driver.findElement(By.id("stat-number-deliveries-done"));

        assertThat(statsNumberOfDeliveriesDone.isDisplayed()).isTrue();
        assertThat(Integer.parseInt(statsNumberOfDeliveriesDone.getText()))
                .isEqualTo(deliveriesManager.statsRiderNumberDeliveriesDone(focusedAccountId));
    }

    @Then("I can see statistics about the number of deliveries requested")
    public void iCanSeeCustomerStatistics() {
        WebElement statsDeliveryRequestRate =
                driver.findElement(By.id("stat-delivery-request-rate"));

        assertThat(statsDeliveryRequestRate.isDisplayed()).isTrue();
        assertThat(Double.parseDouble(statsDeliveryRequestRate.getText()))
                .isEqualTo(deliveriesManager.statsCustomerDeliveryRate(focusedAccountId));
    }

    @Then("I can {not}check the API key")
    public void checkApiKey(boolean not) {
        String apiKeyId = "api-key";
        if (not) assertThatThrownBy(() -> driver.findElement(By.id(apiKeyId)));
        else {
            driver.findElement(By.id(apiKeyId + "-btn")).click();
            WebElement apiKey = driver.findElement(By.id(apiKeyId));
            assertThat(apiKey.getText())
                    .isEqualTo(apiController.apiToken(currentAccount.getEmail()));
        }
    }

    @And("I wait {int} seconds for the auction to end")
    public void iWaitSecondsForTheAuctionToEnd(int secondsToWait) {
        await().atLeast(secondsToWait, TimeUnit.SECONDS);
    }

    @And("I click the check button on the line of the first delivery presented")
    public void iClickTheCheckButtonOnTheLineOfTheFirstDeliveryPresented() {
        List<WebElement> checkButtons = driver.findElements(By.cssSelector("td > button"));
        assertThat(checkButtons).isNotEmpty();

        WebElement button = checkButtons.get(0);
        button.click();
        String[] idSplit = button.getAttribute("id").split("-");
        focusedDeliveryId = Long.parseLong(idSplit[idSplit.length - 1]);
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

    private Delivery getDeliveryId(String customerEmail, double latitude, double longitude) {
        return deliveriesManager.getDeliveriesFromCustomer(customerEmail).stream()
                .filter(
                        delivery ->
                                delivery.getLatitude() == latitude
                                        && delivery.getLongitude() == longitude)
                .findFirst()
                .orElseThrow();
    }

    @Then("graphs of the current metrics is shown")
    public void assertMetricsGraphs() {
        WebElement panel0 = driver.findElement(By.id("panel0"));
        assertThat(panel0.isDisplayed()).isTrue();

        WebElement panel1 = driver.findElement(By.id("panel1"));
        assertThat(panel1.isDisplayed()).isTrue();

        WebElement panel2 = driver.findElement(By.id("panel2"));
        assertThat(panel2.isDisplayed()).isTrue();

        WebElement panel3 = driver.findElement(By.id("panel3"));
        assertThat(panel3.isDisplayed()).isTrue();

        WebElement panel4 = driver.findElement(By.id("panel4"));
        assertThat(panel4.isDisplayed()).isTrue();

        WebElement panel5 = driver.findElement(By.id("panel5"));
        assertThat(panel5.isDisplayed()).isTrue();

        WebElement panel6 = driver.findElement(By.id("panel6"));
        assertThat(panel6.isDisplayed()).isTrue();

        WebElement panel7 = driver.findElement(By.id("panel7"));
        assertThat(panel7.isDisplayed()).isTrue();

        WebElement panel8 = driver.findElement(By.id("panel8"));
        assertThat(panel8.isDisplayed()).isTrue();

        WebElement panel9 = driver.findElement(By.id("panel9"));
        assertThat(panel9.isDisplayed()).isTrue();

        WebElement panel10 = driver.findElement(By.id("panel10"));
        assertThat(panel10.isDisplayed()).isTrue();

        WebElement panel11 = driver.findElement(By.id("panel11"));
        assertThat(panel11.isDisplayed()).isTrue();
    }
}
