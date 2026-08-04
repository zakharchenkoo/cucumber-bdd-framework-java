package com.qaportfolio.pages;

import com.qaportfolio.config.AppConfig;
import com.qaportfolio.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        logger.info("Opening SauceDemo login page");
        driver.get(AppConfig.getInstance().baseUrl());
        waitForVisible(LOGIN_BUTTON);
    }

    public void enterUsername(String username) {
        waitAndType(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        waitAndType(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        waitAndClick(LOGIN_BUTTON);
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
