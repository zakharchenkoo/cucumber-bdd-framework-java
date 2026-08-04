@inventory
Feature: Product Catalog
  As a logged-in customer
  I want to browse the product catalog
  So that I can find products to purchase

  Background:
    Given I am logged in as "standard_user"

  @smoke
  Scenario: All products are displayed on the inventory page
    Then I should see 6 products on the page
    And each product should have a name, price and "Add to cart" button

  @regression
  Scenario: Products can be sorted by price low to high
    When I sort products by "Price (low to high)"
    Then the first product price should be lower than the last product price

  @regression
  Scenario: Products can be sorted by name A to Z
    When I sort products by "Name (A to Z)"
    Then products should be displayed in alphabetical order

  @regression
  Scenario: Product detail page opens correctly
    When I click on product "Sauce Labs Backpack"
    Then I should see the product detail page
    And the product name should be "Sauce Labs Backpack"
    And the product price should be "$29.99"
