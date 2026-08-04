package com.qaportfolio.utils;

import com.qaportfolio.driver.DriverManager;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenshotHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotHelper.class);

    private ScreenshotHelper() {
        // Utility class.
    }

    public static void attachScreenshot(Scenario scenario) {
        if (!DriverManager.hasDriver()) {
            LOGGER.warn("Screenshot was not captured because no driver is available");
            return;
        }

        try {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure - " + scenario.getName());
            LOGGER.info("Failure screenshot attached for scenario '{}'", scenario.getName());
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to capture screenshot for scenario '{}'", scenario.getName(), exception);
        }
    }
}
