@login
Feature: User Authentication
  As a customer
  I want to log in to the SauceDemo application
  So that I can access the product catalog

  Background:
    Given I am on the login page

  @smoke
  Scenario: Successful login with valid credentials
    When I enter username "standard_user" and password "secret_sauce"
    And I click the login button
    Then I should be redirected to the inventory page
    And the page title should be "Products"

  @regression
  Scenario: Login fails with invalid password
    When I enter username "standard_user" and password "wrong_password"
    And I click the login button
    Then I should see the error message "Username and password do not match"

  @regression
  Scenario: Login fails with locked out user
    When I enter username "locked_out_user" and password "secret_sauce"
    And I click the login button
    Then I should see the error message "Sorry, this user has been locked out"

  @regression
  Scenario: Login fails with empty credentials
    When I click the login button
    Then I should see the error message "Username is required"

  @regression
  Scenario Outline: Login with different user types
    When I enter username "<username>" and password "secret_sauce"
    And I click the login button
    Then I should be redirected to the inventory page

    Examples:
      | username                |
      | standard_user           |
      | problem_user            |
      | performance_glitch_user |
