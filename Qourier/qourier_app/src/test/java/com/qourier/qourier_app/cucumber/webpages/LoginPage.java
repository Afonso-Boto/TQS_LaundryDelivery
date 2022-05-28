package com.qourier.qourier_app.cucumber.webpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class LoginPage implements WebPage {

    private final WebDriver driver;

    public LoginPage(WebDriver driver, int port) {
        this.driver = driver;

        driver.get("http://localhost:" +  port);

        PageFactory.initElements(driver, this);
    }

    @Override
    public boolean isOpened() {
        // TODO
        return false;
    }
}
