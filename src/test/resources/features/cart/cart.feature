@cart
Feature: Shopping Cart
  As a logged-in customer
  I want to manage my shopping cart
  So that I can prepare my order

  Background:
    Given I am logged in as "standard_user"

  @smoke
  Scenario: Add a single product to cart
    When I add "Sauce Labs Backpack" to the cart
    Then the cart badge should show "1"
    And "Sauce Labs Backpack" should be in the cart

  @regression
  Scenario: Add multiple products to cart
    When I add "Sauce Labs Backpack" to the cart
    And I add "Sauce Labs Bike Light" to the cart
    Then the cart badge should show "2"

  @regression
  Scenario: Remove product from cart
    Given I have "Sauce Labs Backpack" in my cart
    When I remove "Sauce Labs Backpack" from the cart
    Then the cart should be empty
    And the cart badge should not be visible

  @regression
  Scenario: Cart persists after navigation
    Given I have "Sauce Labs Backpack" in my cart
    When I navigate to the inventory page
    Then the cart badge should still show "1"
