package com.qaportfolio.pages.base;

import com.qaportfolio.config.AppConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    private static final Duration ACTION_CONFIRMATION_TIMEOUT =
            Duration.ofSeconds(3);

    private static final Duration INPUT_CONFIRMATION_TIMEOUT =
            Duration.ofSeconds(3);

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(AppConfig.getInstance().explicitWait())
        );
    }

    protected void waitAndClick(By locator) {
        logger.info("Clicking element located by {}", locator);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        clickElement(element);
    }

    protected void clickElement(WebElement element) {
        scrollToElement(element);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException exception) {
            logger.warn(
                    "Regular click was intercepted. Using JavaScript click as a fallback"
            );
            clickWithJavaScript(element);
        }
    }

    protected void waitAndType(By locator, String text) {
        String expectedValue = text == null ? "" : text;

        logger.info(
                "Typing into element located by {}",
                locator
        );

        waitForDocumentReady();

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        enterTextWithKeyboard(element, expectedValue);

        boolean valueWasStored = waitForCondition(
                inputValueToBe(locator, expectedValue),
                INPUT_CONFIRMATION_TIMEOUT
        );

        if (!valueWasStored) {
            logger.warn(
                    "The value was not retained in element located by {}. "
                            + "Retrying with JavaScript",
                    locator
            );

            WebElement elementForRetry = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );

            setInputValueWithJavaScript(
                    elementForRetry,
                    expectedValue
            );
        }

        wait.until(
                inputValueToBe(locator, expectedValue)
        );
    }

    protected ExpectedCondition<Boolean> inputValueToBe(
            By locator,
            String expectedValue
    ) {
        String normalizedExpectedValue =
                expectedValue == null ? "" : expectedValue;

        return currentDriver -> {
            try {
                WebElement element =
                        currentDriver.findElement(locator);

                String actualValue =
                        element.getDomProperty("value");

                String normalizedActualValue =
                        actualValue == null ? "" : actualValue;

                return normalizedExpectedValue.equals(
                        normalizedActualValue
                );
            } catch (
                    NoSuchElementException
                    | StaleElementReferenceException exception
            ) {
                return false;
            }
        };
    }

    private void enterTextWithKeyboard(
            WebElement element,
            String text
    ) {
        scrollToElement(element);

        element.click();

        element.sendKeys(
                Keys.chord(Keys.CONTROL, "a"),
                Keys.DELETE
        );

        if (!text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected WebElement waitForVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        logger.debug("Waiting for all elements to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected String getText(By locator) {
        String text = waitForVisible(locator).getText().trim();
        logger.debug("Read text '{}' from {}", text, locator);
        return text;
    }

    protected boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    protected void scrollToElement(WebElement element) {
        logger.debug("Scrolling element into the viewport");
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                element
        );
    }

    protected void clickWithJavaScript(WebElement element) {
        logger.warn("Clicking element with JavaScript as a fallback");
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }

    protected void clickAndWait(
            By locator,
            ExpectedCondition<?> postCondition,
            String actionDescription
    ) {
        waitForDocumentReady();

        logger.info(
                "{} using element located by {}",
                actionDescription,
                locator
        );

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        clickElement(element);

        boolean actionCompleted = waitForCondition(
                postCondition,
                ACTION_CONFIRMATION_TIMEOUT
        );

        if (!actionCompleted) {
            logger.warn(
                    "{} did not complete after the regular click. "
                            + "Retrying with JavaScript",
                    actionDescription
            );

            WebElement elementForRetry = wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );

            scrollToElement(elementForRetry);
            clickWithJavaScript(elementForRetry);
        }

        wait.until(postCondition);
    }

    protected boolean waitForCondition(
            ExpectedCondition<?> condition,
            Duration timeout
    ) {
        try {
            new WebDriverWait(driver, timeout).until(condition);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected void setInputValueWithJavaScript(
            WebElement element,
            String value
    ) {
        logger.warn(
                "Setting input value with JavaScript as a fallback"
        );

        ((JavascriptExecutor) driver).executeScript(
                """
                const input = arguments[0];
                const value = arguments[1];
    
                const valueSetter =
                    Object.getOwnPropertyDescriptor(
                        window.HTMLInputElement.prototype,
                        'value'
                    ).set;
    
                valueSetter.call(input, value);
    
                input.dispatchEvent(
                    new Event('input', { bubbles: true })
                );
    
                input.dispatchEvent(
                    new Event('change', { bubbles: true })
                );
                """,
                element,
                value
        );
    }

    protected void waitForDocumentReady() {
        wait.until(currentDriver ->
                "complete".equals(
                        ((JavascriptExecutor) currentDriver)
                                .executeScript(
                                        "return document.readyState"
                                )
                )
        );
    }
}
