package com.qaportfolio.pages;

import com.qaportfolio.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Locale;

public class CartPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By CART_CONTENTS = By.cssSelector("[data-test='cart-contents-container']");
    private static final By CART_ITEMS = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final String REMOVE_PRODUCT_BUTTON = "[data-test='remove-%s']";
    private static final By CONTINUE_SHOPPING_BUTTON = By.id("continue-shopping");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.and(
                    ExpectedConditions.urlContains("cart.html"),
                    ExpectedConditions.textToBe(PAGE_TITLE, "Your Cart"),
                    ExpectedConditions.visibilityOfElementLocated(CART_CONTENTS)
            ));
        } catch (TimeoutException exception) {
            logger.warn(
                    "Cart page was not loaded. Current URL: {}",
                    driver.getCurrentUrl()
            );
            return false;
        }
    }

    public boolean isProductPresent(String productName) {
        ensureLoaded();

        return waitForAllVisible(CART_ITEMS).stream()
                .anyMatch(item ->
                        productName.equals(
                                item.findElement(ITEM_NAME).getText().trim()
                        )
                );
    }

    public void removeProduct(String productName) {
        ensureLoaded();

        logger.info(
                "Removing product '{}' from the cart",
                productName
        );

        if (!isProductPresent(productName)) {
            throw new IllegalArgumentException(
                    "Product is not present in the cart: " + productName
            );
        }

        int previousItemCount = driver.findElements(CART_ITEMS).size();

        String productSlug = toProductSlug(productName);

        By removeButton = By.cssSelector(
                REMOVE_PRODUCT_BUTTON.formatted(productSlug)
        );

        clickAndWait(
                removeButton,
                ExpectedConditions.numberOfElementsToBe(
                        CART_ITEMS,
                        previousItemCount - 1
                ),
                "Removing product '" + productName + "' from the cart"
        );

        logger.info(
                "Product '{}' was removed from the cart",
                productName
        );
    }

    public boolean isEmpty() {
        ensureLoaded();
        return driver.findElements(CART_ITEMS).isEmpty();
    }

    public boolean isCartBadgeVisible() {
        ensureLoaded();
        return isVisible(CART_BADGE);
    }

    public String getCartBadgeText() {
        ensureLoaded();
        return getText(CART_BADGE);
    }

    public void continueShopping() {
        ensureLoaded();

        clickAndWait(
                CONTINUE_SHOPPING_BUTTON,
                ExpectedConditions.urlContains("inventory.html"),
                "Returning to the inventory page"
        );
    }

    public void proceedToCheckout() {
        ensureLoaded();

        clickAndWait(
                CHECKOUT_BUTTON,
                ExpectedConditions.urlContains("checkout-step-one.html"),
                "Opening the checkout page"
        );

        logger.info("Checkout page was opened");
    }

    private void ensureLoaded() {
        if (!isLoaded()) {
            throw new IllegalStateException(
                    "Expected the cart page, but the current URL is: "
                            + driver.getCurrentUrl()
            );
        }
    }

    private String toProductSlug(String productName) {
        return productName.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "-");
    }
}
