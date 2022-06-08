package tqs.project.laundryplatform.cucumber.steps;

import static org.hamcrest.MatcherAssert.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.CoreMatchers;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import tqs.project.laundryplatform.repository.UserRepository;

public class MyStepdefs {

    private static WebDriver driver;
    private final UserRepository userRepository;

    public MyStepdefs(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                CoreMatchers.is("LAUNDRY & DRY CLEANING"));
    }

    @And("I click the {string} option")
    public void iClickTheOption(String arg0) {}

    @Then("I select the {string} for the type")
    public void iSelectTheForTheType(String arg0) {}

    @And("I select the {string} for the color")
    public void iSelectTheForTheColor(String arg0) {}

    @And("I select {string} as the number")
    public void iSelectAsTheNumber(String arg0) {}

    @Then("I click the {string} button")
    public void iClickTheButton(String arg0) {}

    @Then("I should see the {string} in the table")
    public void iShouldSeeTheInTheTable(String arg0) {}

    @And("I should see {string} in the table")
    public void iShouldSeeInTheTable(String arg0) {}

    @Then("I select the {string} for the typeCucum")
    public void iSelectTheForTheTypeCucum(String arg0) {}

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
}
