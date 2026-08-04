package com.qaportfolio.steps;

import com.qaportfolio.driver.DriverManager;
import com.qaportfolio.pages.InventoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class InventorySteps {

    @Then("I should see {int} products on the page")
    public void iShouldSeeProductsOnThePage(int expectedCount) {
        assertThat(inventoryPage().getProductCount()).isEqualTo(expectedCount);
    }

    @Then("each product should have a name, price and {string} button")
    public void eachProductShouldHaveRequiredData(String expectedButtonText) {
        assertThat(expectedButtonText).isEqualTo("Add to cart");
        assertThat(inventoryPage().allProductsHaveRequiredData()).isTrue();
    }

    @When("I sort products by {string}")
    public void iSortProductsBy(String sortOption) {
        inventoryPage().sortBy(sortOption);
    }

    @Then("the first product price should be lower than the last product price")
    public void theFirstProductPriceShouldBeLowerThanTheLastProductPrice() {
        assertThat(inventoryPage().getFirstProductPrice())
                .isLessThan(inventoryPage().getLastProductPrice());
    }

    @Then("products should be displayed in alphabetical order")
    public void productsShouldBeDisplayedInAlphabeticalOrder() {
        assertThat(inventoryPage().areProductsAlphabeticallySorted()).isTrue();
    }

    @When("I click on product {string}")
    public void iClickOnProduct(String productName) {
        inventoryPage().openProduct(productName);
    }

    @Then("I should see the product detail page")
    public void iShouldSeeTheProductDetailPage() {
        assertThat(inventoryPage().isProductDetailPageVisible()).isTrue();
    }

    @Then("the product name should be {string}")
    public void theProductNameShouldBe(String expectedName) {
        assertThat(inventoryPage().getProductDetailName()).isEqualTo(expectedName);
    }

    @Then("the product price should be {string}")
    public void theProductPriceShouldBe(String expectedPrice) {
        assertThat(inventoryPage().getProductDetailPrice()).isEqualTo(expectedPrice);
    }

    private InventoryPage inventoryPage() {
        return new InventoryPage(DriverManager.getDriver());
    }
}
