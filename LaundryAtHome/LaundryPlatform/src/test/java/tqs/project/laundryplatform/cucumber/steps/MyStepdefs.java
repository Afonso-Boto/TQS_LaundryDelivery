package tqs.project.laundryplatform.cucumber.steps;

import static org.hamcrest.MatcherAssert.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.CoreMatchers;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import tqs.project.laundryplatform.repository.OrderRepository;
import tqs.project.laundryplatform.repository.UserRepository;

public class MyStepdefs {

    private static WebDriver driver;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public MyStepdefs(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @When("I navigate to {string}")
    public void iNavigateTo(String arg0) {
        driver = new HtmlUnitDriver(true);
        driver.get(arg0);
        driver.manage().window().setSize(new Dimension(1512, 886));
    }

    @And("I set the username as {string}")
    public void iSetTheUsernameAs(String arg0) {
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys(arg0);
    }

    @And("I set the password as {string}")
    public void iSetThePasswordAs(String arg0) {
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys(arg0);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        driver.findElement(By.id("login")).click();
    }

    @Then("I should see the index page")
    public void iShouldSeeTheIndexPage() {
        assertThat(
                driver.findElement(By.cssSelector(".active .text-uppercase")).getText(),
                CoreMatchers.is("Laundry & Dry Cleaning"));
    }

//    @And("I click the wash option")
//    public void iClickTheWashOption() {
//        driver.findElement(By.cssSelector(".col-lg-3:nth-child(1) .font-weight-bold")).click();
//    }
//
//    @Then("I select the {string} for the type")
//    public void iSelectTheForTheType(String arg0) {
//        assertThat(driver.findElement(By.cssSelector(".mb-4")).getText(), is("Our Services"));
//
//        WebElement dropdown = driver.findElement(By.id("type"));
//        dropdown.findElement(By.xpath("//option[. = '%s']".formatted(arg0))).click();
//    }
//
//    @And("I select the {string} for the color")
//    public void iSelectTheForTheColor(String arg0) {
//        WebElement dropdown = driver.findElement(By.id("color"));
//        dropdown.findElement(By.xpath("//option[. = '%s']".formatted(arg0))).click();
//    }
//
//    @And("I select {string} as the number")
//    public void iSelectAsTheNumber(String arg0) {
//        driver.findElement(By.id("number")).click();
//        driver.findElement(By.id("number")).sendKeys(arg0);
//    }
//
//    @Then("I click the add button")
//    public void iClickTheAddButton() {
//        driver.findElement(By.id("btnAdd")).click();
//    }
//
//    @Then("I click the make order button")
//    public void iClickTheMakeOrderButton() {
//        driver.findElement(By.id("btnMakeOrder")).click();
//        assertThat(driver.switchTo().alert().getText(), is("Order submitted successfully!"));
//    }

    @And("I set the your name as {string}")
    public void iSetTheYourNameAs(String arg0) {
        driver.findElement(By.id("fullName")).click();
        driver.findElement(By.id("fullName")).sendKeys(arg0);
    }

    @And("I set the phone number as {string}")
    public void iSetThePhoneNumberAs(String arg0) {
        driver.findElement(By.id("phoneNumber")).click();
        driver.findElement(By.id("phoneNumber")).sendKeys(arg0);
    }

    @And("I set the email as {string}")
    public void iSetTheEmailAs(String arg0) {
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys(arg0);
    }

    @And("I click the register button")
    public void iClickTheRegisterButton() {
        driver.findElement(By.id("register")).click();
    }

//    @Given("I am login as {string}")
//    public void iAmLoginAs(String arg0) {
//        User user = userRepository.findByUsername(arg0).orElseThrow();
//
//
//        iNavigateTo("http://localhost:8080/");
//        driver.manage().addCookie(new Cookie("id", user.getUsername()));
//        iNavigateTo("http://localhost:8080/index");
//        driver.manage().window().setSize(new Dimension(1512, 886));
//    }
}
