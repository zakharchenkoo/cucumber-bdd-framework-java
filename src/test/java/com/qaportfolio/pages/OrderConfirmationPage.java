package com.qaportfolio.pages;

import com.qaportfolio.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderConfirmationPage extends BasePage {

    private static final By CONFIRMATION_MESSAGE = By.cssSelector("[data-test='complete-header']");

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public String getConfirmationMessage() {
        return getText(CONFIRMATION_MESSAGE);
    }
}
