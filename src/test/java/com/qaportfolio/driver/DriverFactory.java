package com.qaportfolio.driver;

import com.qaportfolio.config.AppConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public final class DriverFactory {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
        // Factory class.
    }

    public static WebDriver createDriver() {
        AppConfig config = AppConfig.getInstance();

        String browser = config.browser()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!"chrome".equals(browser)) {
            throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
            );
        }

        LOGGER.info(
                "Creating ChromeDriver; headless={}",
                config.headless()
        );

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Wait until the page and its resources are fully loaded.
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--window-size=1920,1080");

        if (config.headless()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        return new ChromeDriver(options);
    }
}