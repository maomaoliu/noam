package com.thoughtworks.maomao.integration.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;

public class Browser {

    private final boolean javascriptEnabled;
    private final String hostAddress;
    private final WebDriver driver;

    public Browser(String hostAddress, boolean javascriptEnabled) {
        this.hostAddress = hostAddress;
        this.driver = new HtmlUnitDriver();
        this.javascriptEnabled = javascriptEnabled;
        this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public Browser open(String url) {
        if (url.startsWith("/")) {
            driver.get(hostAddress + url);
        } else {
            driver.get(url);
        }
        return this;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void stop() {
        try {
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
