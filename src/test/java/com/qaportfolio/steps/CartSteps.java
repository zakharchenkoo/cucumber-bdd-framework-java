package com.qaportfolio.steps;

import com.qaportfolio.driver.DriverManager;
import com.qaportfolio.pages.CartPage;
import com.qaportfolio.pages.InventoryPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CartSteps {

    @When("I add {string} to the cart")
    public void iAddToTheCart(String productName) {
        inventoryPage().addProductToCart(productName);
    }

    @Then("the cart badge should show {string}")
    public void theCartBadgeShouldShow(String expectedCount) {
        assertThat(inventoryPage().getCartBadgeText()).isEqualTo(expectedCount);
    }

    @Then("{string} should be in the cart")
    public void productShouldBeInTheCart(String productName) {
        inventoryPage().openCart();

        CartPage cartPage = cartPage();

        assertThat(cartPage.isLoaded())
                .as("The shopping cart page should be loaded")
                .isTrue();

        assertThat(cartPage.isProductPresent(productName))
                .as("Product '%s' should be present in the cart", productName)
                .isTrue();
    }

    @Given("I have {string} in my cart")
    public void iHaveProductInMyCart(String productName) {
        InventoryPage inventoryPage = inventoryPage();

        inventoryPage.addProductToCart(productName);
        inventoryPage.openCart();

        CartPage cartPage = cartPage();

        assertThat(cartPage.isLoaded())
                .as("The shopping cart page should be loaded")
                .isTrue();

        assertThat(cartPage.isProductPresent(productName))
                .as("Product '%s' should be present in the cart", productName)
                .isTrue();
    }

    @When("I remove {string} from the cart")
    public void iRemoveFromTheCart(String productName) {
        cartPage().removeProduct(productName);
    }

    @Then("the cart should be empty")
    public void theCartShouldBeEmpty() {
        assertThat(cartPage().isEmpty()).isTrue();
    }

    @Then("the cart badge should not be visible")
    public void theCartBadgeShouldNotBeVisible() {
        assertThat(cartPage().isCartBadgeVisible()).isFalse();
    }

    @When("I navigate to the inventory page")
    public void iNavigateToTheInventoryPage() {
        cartPage().continueShopping();
        assertThat(inventoryPage().isLoaded()).isTrue();
    }

    @Then("the cart badge should still show {string}")
    public void theCartBadgeShouldStillShow(String expectedCount) {
        assertThat(inventoryPage().getCartBadgeText()).isEqualTo(expectedCount);
    }

    private InventoryPage inventoryPage() {
        return new InventoryPage(DriverManager.getDriver());
    }

    private CartPage cartPage() {
        return new CartPage(DriverManager.getDriver());
    }
}
