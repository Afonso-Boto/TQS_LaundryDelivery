package com.qourier.qourier_app.cucumber.steps;

import lombok.AccessLevel;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WebDriverHolder {

    @Getter(AccessLevel.PACKAGE)
    private static WebDriver driver;

    static WebDriver initDriver() {
        driver = new HtmlUnitDriver(true);
        return driver;
    }
}
