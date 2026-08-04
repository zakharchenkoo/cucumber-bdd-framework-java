package com.qaportfolio.steps;

import com.qaportfolio.driver.DriverManager;
import com.qaportfolio.pages.CartPage;
import com.qaportfolio.pages.CheckoutPage;
import com.qaportfolio.pages.OrderConfirmationPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutSteps {

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        new CartPage(DriverManager.getDriver()).proceedToCheckout();
    }

    @When("I enter first name {string}, last name {string} and postal code {string}")
    public void iEnterCustomerInformation(String firstName, String lastName, String postalCode) {
        checkoutPage().enterCustomerInformation(firstName, lastName, postalCode);
    }

    @When("I continue to the order summary")
    public void iContinueToTheOrderSummary() {
        checkoutPage().clickContinue();
    }

    @When("I confirm the order")
    public void iConfirmTheOrder() {
        checkoutPage().confirmOrder();
    }

    @Then("I should see the order confirmation message {string}")
    public void iShouldSeeTheOrderConfirmationMessage(String expectedMessage) {
        OrderConfirmationPage confirmationPage = new OrderConfirmationPage(DriverManager.getDriver());
        assertThat(confirmationPage.getConfirmationMessage()).isEqualTo(expectedMessage);
    }

    @When("I click continue without filling the form")
    public void iClickContinueWithoutFillingTheForm() {
        checkoutPage().clickContinueExpectingValidationError();
    }

    @When("I click continue")
    public void iClickContinue() {
        checkoutPage().clickContinueExpectingValidationError();
    }

    @Then("I should see the error {string}")
    public void iShouldSeeTheError(String expectedMessage) {
        assertThat(checkoutPage().getErrorMessage()).contains(expectedMessage);
    }

    @Then("the order summary should contain {string}")
    public void theOrderSummaryShouldContain(String productName) {
        assertThat(checkoutPage().summaryContainsProduct(productName)).isTrue();
    }

    @Then("the item total should be {string}")
    public void theItemTotalShouldBe(String expectedTotal) {
        assertThat(checkoutPage().getItemTotal()).isEqualTo(expectedTotal);
    }

    private CheckoutPage checkoutPage() {
        return new CheckoutPage(DriverManager.getDriver());
    }
}
