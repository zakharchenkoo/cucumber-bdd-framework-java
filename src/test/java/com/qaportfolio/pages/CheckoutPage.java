package com.qaportfolio.pages;

import com.qaportfolio.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutPage extends BasePage {

    private static final By PAGE_TITLE =
            By.cssSelector("[data-test='title']");

    private static final By FIRST_NAME_INPUT =
            By.id("first-name");

    private static final By LAST_NAME_INPUT =
            By.id("last-name");

    private static final By POSTAL_CODE_INPUT =
            By.id("postal-code");

    private static final By CONTINUE_BUTTON =
            By.id("continue");

    private static final By FINISH_BUTTON =
            By.id("finish");

    private static final By ERROR_MESSAGE =
            By.cssSelector("[data-test='error']");

    private static final By SUMMARY_ITEM_NAMES =
            By.cssSelector("[data-test='inventory-item-name']");

    private static final By ITEM_TOTAL =
            By.cssSelector("[data-test='subtotal-label']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void enterCustomerInformation(
            String firstName,
            String lastName,
            String postalCode
    ) {
        ensureInformationPageLoaded();

        logger.info("Entering checkout customer information");

        waitAndType(FIRST_NAME_INPUT, firstName);
        waitAndType(LAST_NAME_INPUT, lastName);
        waitAndType(POSTAL_CODE_INPUT, postalCode);

        wait.until(
                ExpectedConditions.and(
                        inputValueToBe(
                                FIRST_NAME_INPUT,
                                firstName
                        ),
                        inputValueToBe(
                                LAST_NAME_INPUT,
                                lastName
                        ),
                        inputValueToBe(
                                POSTAL_CODE_INPUT,
                                postalCode
                        )
                )
        );

        logger.info(
                "Checkout customer information was entered successfully"
        );
    }

    public void clickContinue() {
        ensureInformationPageLoaded();

        clickAndWait(
                CONTINUE_BUTTON,
                ExpectedConditions.and(
                        ExpectedConditions.urlContains(
                                "checkout-step-two.html"
                        ),
                        ExpectedConditions.textToBe(
                                PAGE_TITLE,
                                "Checkout: Overview"
                        ),
                        ExpectedConditions.visibilityOfElementLocated(
                                FINISH_BUTTON
                        )
                ),
                "Continuing to the order summary"
        );

        logger.info("Order summary page was opened");
    }

    public void clickContinueExpectingValidationError() {
        ensureInformationPageLoaded();

        clickAndWait(
                CONTINUE_BUTTON,
                ExpectedConditions.visibilityOfElementLocated(
                        ERROR_MESSAGE
                ),
                "Submitting invalid checkout information"
        );

        logger.info(
                "Checkout validation error was displayed"
        );
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean summaryContainsProduct(String productName) {
        ensureSummaryPageLoaded();

        return waitForAllVisible(SUMMARY_ITEM_NAMES).stream()
                .map(element -> element.getText().trim())
                .anyMatch(productName::equals);
    }

    public String getItemTotal() {
        ensureSummaryPageLoaded();

        return getText(ITEM_TOTAL)
                .replace("Item total:", "")
                .trim();
    }

    public void confirmOrder() {
        ensureSummaryPageLoaded();

        clickAndWait(
                FINISH_BUTTON,
                ExpectedConditions.and(
                        ExpectedConditions.urlContains(
                                "checkout-complete.html"
                        ),
                        ExpectedConditions.textToBe(
                                PAGE_TITLE,
                                "Checkout: Complete!"
                        )
                ),
                "Confirming the order"
        );

        logger.info("Order was confirmed");
    }

    private void ensureInformationPageLoaded() {
        wait.until(
                ExpectedConditions.and(
                        ExpectedConditions.urlContains(
                                "checkout-step-one.html"
                        ),
                        ExpectedConditions.textToBe(
                                PAGE_TITLE,
                                "Checkout: Your Information"
                        ),
                        ExpectedConditions.visibilityOfElementLocated(
                                FIRST_NAME_INPUT
                        ),
                        ExpectedConditions.visibilityOfElementLocated(
                                CONTINUE_BUTTON
                        )
                )
        );
    }

    private void ensureSummaryPageLoaded() {
        wait.until(
                ExpectedConditions.and(
                        ExpectedConditions.urlContains(
                                "checkout-step-two.html"
                        ),
                        ExpectedConditions.textToBe(
                                PAGE_TITLE,
                                "Checkout: Overview"
                        ),
                        ExpectedConditions.visibilityOfElementLocated(
                                FINISH_BUTTON
                        )
                )
        );
    }
}