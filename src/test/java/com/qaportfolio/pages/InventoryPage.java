package com.qaportfolio.pages;

import com.qaportfolio.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class InventoryPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By INVENTORY_ITEMS = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_ACTION_BUTTON = By.cssSelector("button.btn_inventory");
    private static final String ADD_TO_CART_BUTTON = "[data-test='add-to-cart-%s']";
    private static final String REMOVE_FROM_CART_BUTTON = "[data-test='remove-%s']";
    private static final String PRODUCT_NAME_XPATH = "//div[@data-test='inventory-item-name' and normalize-space()=\"%s\"]";
    private static final By SORT_SELECT = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By DETAIL_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By DETAIL_PRICE = By.cssSelector("[data-test='inventory-item-price']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("inventory.html")
                && "Products".equals(getPageTitle());
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public int getProductCount() {
        return waitForAllVisible(INVENTORY_ITEMS).size();
    }

    public boolean allProductsHaveRequiredData() {
        for (WebElement item : waitForAllVisible(INVENTORY_ITEMS)) {
            String name = item.findElement(ITEM_NAME).getText().trim();
            String price = item.findElement(ITEM_PRICE).getText().trim();
            String action = item.findElement(ITEM_ACTION_BUTTON).getText().trim();

            if (name.isEmpty() || !price.matches("\\$\\d+\\.\\d{2}") || !"Add to cart".equals(action)) {
                return false;
            }
        }
        return true;
    }

    public void sortBy(String visibleText) {
        logger.info("Sorting products by '{}'", visibleText);
        WebElement selectElement = waitForVisible(SORT_SELECT);
        new Select(selectElement).selectByVisibleText(visibleText);
    }

    public BigDecimal getFirstProductPrice() {
        return getProductPrices().getFirst();
    }

    public BigDecimal getLastProductPrice() {
        return getProductPrices().getLast();
    }

    public boolean areProductsAlphabeticallySorted() {
        List<String> actualNames = getProductNames();
        List<String> sortedNames = new ArrayList<>(actualNames);
        sortedNames.sort(Comparator.naturalOrder());
        return actualNames.equals(sortedNames);
    }

    public void openProduct(String productName) {
        By productNameLink = By.xpath(PRODUCT_NAME_XPATH.formatted(productName));
        clickAndWait(
                productNameLink,
                ExpectedConditions.urlContains("inventory-item.html"),
                "Opening product details for '" + productName + "'"
        );
    }

    public boolean isProductDetailPageVisible() {
        return driver.getCurrentUrl().contains("inventory-item.html") && isVisible(DETAIL_NAME);
    }

    public String getProductDetailName() {
        return getText(DETAIL_NAME);
    }

    public String getProductDetailPrice() {
        return getText(DETAIL_PRICE);
    }

    public void addProductToCart(String productName) {
        logger.info("Adding product '{}' to the cart", productName);

        String productSlug = toProductSlug(productName);

        By addButton = By.cssSelector(
                ADD_TO_CART_BUTTON.formatted(productSlug)
        );

        By removeButton = By.cssSelector(
                REMOVE_FROM_CART_BUTTON.formatted(productSlug)
        );

        if (isVisible(removeButton)) {
            throw new IllegalStateException(
                    "Product is already in the cart: " + productName
            );
        }

        int previousCartCount = getCartItemCount();

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(addButton)
        );

        clickElement(button);

        boolean cartUpdatedAfterRegularClick = waitForCartUpdate(
                removeButton,
                previousCartCount,
                Duration.ofSeconds(2)
        );

        if (!cartUpdatedAfterRegularClick) {
            logger.warn(
                    "Regular click did not update the cart for '{}'. "
                            + "Retrying with JavaScript",
                    productName
            );

            WebElement buttonForRetry = wait.until(
                    ExpectedConditions.elementToBeClickable(addButton)
            );

            clickWithJavaScript(buttonForRetry);
        }

        wait.until(driver ->
                isVisible(removeButton)
                        || getCartItemCount() == previousCartCount + 1
        );

        logger.info(
                "Product '{}' was added to the cart. Cart count: {}",
                productName,
                getCartItemCount()
        );
    }

    public void openCart() {
        clickAndWait(
                CART_LINK,
                ExpectedConditions.urlContains("cart.html"),
                "Opening the shopping cart"
        );

        logger.info("Shopping cart page was opened");
    }

    public String getCartBadgeText() {
        return getText(CART_BADGE);
    }

    public boolean isCartBadgeVisible() {
        return isVisible(CART_BADGE);
    }

    private WebElement findProductCard(String productName) {
        return waitForAllVisible(INVENTORY_ITEMS).stream()
                .filter(item -> productName.equals(item.findElement(ITEM_NAME).getText().trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product was not found: " + productName));
    }

    private List<String> getProductNames() {
        return waitForAllVisible(ITEM_NAME).stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    private List<BigDecimal> getProductPrices() {
        return waitForAllVisible(ITEM_PRICE).stream()
                .map(element -> element.getText().replace("$", "").trim())
                .map(BigDecimal::new)
                .toList();
    }

    private int getCartItemCount() {
        return driver.findElements(CART_BADGE).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    private boolean waitForCartUpdate(
            By removeButton,
            int previousCartCount,
            Duration timeout
    ) {
        try {
            return new WebDriverWait(driver, timeout).until(currentDriver ->
                    isVisible(removeButton)
                            || getCartItemCount() == previousCartCount + 1
            );
        } catch (TimeoutException exception) {
            return false;
        }
    }

    private String toProductSlug(String productName) {
        return productName.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "-");
    }
}
