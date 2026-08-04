package com.qaportfolio.steps;

import com.qaportfolio.driver.DriverManager;
import com.qaportfolio.pages.InventoryPage;
import com.qaportfolio.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private LoginPage loginPage;

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.open();
    }

    @Given("I am logged in as {string}")
    public void iAmLoggedInAs(String username) {
        loginPage = new LoginPage(DriverManager.getDriver());
        loginPage.open();
        loginPage.loginAs(username, "secret_sauce");

        assertThat(new InventoryPage(DriverManager.getDriver()).isLoaded())
                .as("The inventory page should be loaded after login")
                .isTrue();
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        currentLoginPage().enterUsername(username);
        currentLoginPage().enterPassword(password);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        currentLoginPage().clickLogin();
    }

    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());
        assertThat(inventoryPage.isLoaded()).isTrue();
    }

    @Then("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        InventoryPage inventoryPage = new InventoryPage(DriverManager.getDriver());
        assertThat(inventoryPage.getPageTitle()).isEqualTo(expectedTitle);
    }

    @Then("I should see the error message {string}")
    public void iShouldSeeTheErrorMessage(String expectedMessage) {
        assertThat(currentLoginPage().getErrorMessage()).contains(expectedMessage);
    }

    private LoginPage currentLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(DriverManager.getDriver());
        }
        return loginPage;
    }
}
