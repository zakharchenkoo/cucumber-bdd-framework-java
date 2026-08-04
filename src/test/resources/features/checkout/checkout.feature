@checkout
Feature: Checkout Process
  As a logged-in customer
  I want to complete the checkout process
  So that I can place my order

  Background:
    Given I am logged in as "standard_user"
    And I have "Sauce Labs Backpack" in my cart

  @smoke
  Scenario: Complete successful checkout
    When I proceed to checkout
    And I enter first name "Anna", last name "Schmidt" and postal code "10115"
    And I continue to the order summary
    And I confirm the order
    Then I should see the order confirmation message "Thank you for your order!"

  @regression
  Scenario: Checkout fails with empty first name
    When I proceed to checkout
    And I click continue without filling the form
    Then I should see the error "First Name is required"

  @regression
  Scenario: Checkout fails with empty last name
    When I proceed to checkout
    And I enter first name "Anna", last name "" and postal code "10115"
    And I click continue
    Then I should see the error "Last Name is required"

  @regression
  Scenario: Order summary shows correct product and price
    When I proceed to checkout
    And I enter first name "Anna", last name "Schmidt" and postal code "10115"
    And I continue to the order summary
    Then the order summary should contain "Sauce Labs Backpack"
    And the item total should be "$29.99"
