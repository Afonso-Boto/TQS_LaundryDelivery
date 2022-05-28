package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.cucumber.webpages.LoginPage;
import com.qourier.qourier_app.cucumber.webpages.NullPage;
import com.qourier.qourier_app.cucumber.webpages.WebPage;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class CucumberSteps {

    private final WebDriver driver = new HtmlUnitDriver(true);

    private WebPage currentPage;

    @Given("I am in the {page} page")
    public void IAmInPage(WebPage page) {

    }

    @ParameterType("\\w+")
    public WebPage page(String pageName) {
        return switch (pageName) {
            case "Login" -> new LoginPage(driver, 8080);
            default -> new NullPage();
        };
    }

}
