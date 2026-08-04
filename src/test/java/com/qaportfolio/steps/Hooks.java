package com.qaportfolio.steps;

import com.qaportfolio.config.AppConfig;
import com.qaportfolio.driver.DriverFactory;
import com.qaportfolio.driver.DriverManager;
import com.qaportfolio.utils.ScreenshotHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("Starting scenario: {}", scenario.getName());
        WebDriver driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);

        try {
            driver.manage().window().maximize();
        } catch (WebDriverException exception) {
            LOGGER.warn("Window maximization is not supported; applying a fixed viewport");
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }

        driver.get(AppConfig.getInstance().baseUrl());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                ScreenshotHelper.attachScreenshot(scenario);
            }
        } finally {
            DriverManager.quit();
            LOGGER.info("Finished scenario: {} with status {}", scenario.getName(), scenario.getStatus());
        }
    }
}
