package com.qourier.qourier_app.cucumber.steps;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WebDriverHolder {

    @Getter(AccessLevel.PACKAGE)
    private static WebDriver driver;

    static WebDriver initDriver() {
//        driver = new HtmlUnitDriver(true);
        driver = WebDriverManager.firefoxdriver().create();
        return driver;
    }
}
