# Cucumber BDD Framework Java

[![BDD Tests](https://github.com/zakharchenkoo/cucumber-bdd-framework-java/actions/workflows/ci.yml/badge.svg)](https://github.com/zakharchenkoo/cucumber-bdd-framework-java/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber-7.34.6-23D96C?logo=cucumber&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.46.0-43B02A?logo=selenium&logoColor=white)
![Allure](https://img.shields.io/badge/Allure-2.35.2-orange)

## About

BDD test framework for the SauceDemo e-commerce application.

## Tech Stack

| Category | Technology | Version |
|---|---|---:|
| Language | Java | 21 |
| BDD | Cucumber JVM | 7.34.6 |
| Test runner | JUnit 5 Platform Suite | 1.14.2 |
| UI automation | Selenium WebDriver | 4.46.0 |
| Driver management | WebDriverManager | 6.3.4 |
| Reporting | Allure Cucumber 7 JVM | 2.35.2 |
| Assertions | AssertJ | 3.27.7 |
| Configuration | Owner | 1.0.12 |
| Test data | JavaFaker | 1.0.2 |
| Logging | SLF4J + Logback | 2.0.18 / 1.5.34 |
| Build | Maven | 3.9+ recommended |
| CI/CD | GitHub Actions | — |

## Architecture

```text
Feature files (Gherkin)
        ↓
Step Definitions
        ↓
Page Objects → BasePage → DriverManager → WebDriver
        ↓
Allure Report
```

The Gherkin layer expresses business behavior without browser implementation details. Step definitions translate readable scenarios into reusable application actions. Page Objects own selectors and UI interactions, while `BasePage` centralizes explicit waits, scrolling, typing, click recovery, and logging. `DriverManager` stores one driver per execution thread, allowing scenarios to run safely in parallel.

## Why BDD

BDD scenarios are readable by QA engineers, developers, product owners, and other stakeholders. They separate expected business behavior from Selenium implementation details. Feature files become executable documentation that stays close to the automated checks. Tags make it possible to select critical paths, full regression coverage, or a functional area for targeted execution.

## Test Coverage

| Feature | Executed scenarios | Main coverage | Tags |
|---|---:|---|---|
| Authentication | 7 | Valid login, invalid password, locked user, empty credentials, multiple user types | `@login`, `@smoke`, `@regression` |
| Product catalog | 4 | Product cards, price sorting, name sorting, product details | `@inventory`, `@smoke`, `@regression` |
| Shopping cart | 4 | Add one or multiple items, remove item, navigation persistence | `@cart`, `@smoke`, `@regression` |
| Checkout | 4 | Successful order, required fields, order summary and item total | `@checkout`, `@smoke`, `@regression` |
| **Total** | **19** | End-to-end SauceDemo customer journey | — |

Scenario Outline example rows are counted as separate executable scenarios.

## Prerequisites

- Java 21
- Maven 3.9 or newer
- Google Chrome
- Git

Check the local environment:

```bash
java -version
mvn -version
google-chrome --version
```

On Windows, Chrome can also be installed in its standard location; WebDriverManager resolves the compatible ChromeDriver automatically.

## Quick Start

```bash
git clone https://github.com/zakharchenkoo/cucumber-bdd-framework-java.git
cd cucumber-bdd-framework-java
mvn clean test -Dtest=SmokeTestRunner
```

## Run Options

Run the smoke suite:

```bash
mvn clean test -Dtest=SmokeTestRunner
```

Run the complete regression suite:

```bash
mvn clean test -Dtest=RegressionTestRunner
```

Run in headless mode:

```bash
mvn clean test -Dheadless=true -Dtest=RegressionTestRunner
```

Run a functional tag directly with the Cucumber engine:

```bash
mvn clean test -Dtest=RegressionTestRunner -Dcucumber.filter.tags="@cart and @regression"
```

Override the base URL or wait timeout:

```bash
mvn clean test \
  -Dbase.url=https://www.saucedemo.com \
  -Dexplicit.wait=15 \
  -Dheadless=true \
  -Dtest=SmokeTestRunner
```

Generate and open an Allure report after a test run:

```bash
mvn allure:serve
```

Generate a static report:

```bash
mvn allure:report
```

The generated report is written to `target/allure-report`.

## Parallel Execution

Cucumber scenario-level parallel execution is enabled in `src/test/resources/junit-platform.properties` with four worker threads. Each scenario receives an isolated browser instance through `ThreadLocal<WebDriver>`. The number of workers can be changed in that file when the execution environment has different CPU or memory limits.

## Configuration

Default settings are stored in `config/app.properties`:

```properties
base.url=https://www.saucedemo.com
browser=chrome
headless=false
explicit.wait=10
```

Every value can be overridden with a JVM system property, for example `-Dheadless=true`.

## Sample Feature

```gherkin
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
```

## CI/CD

GitHub Actions runs the smoke suite on pushes and pull requests to `main`. The regression suite runs for pull requests. Both jobs generate a static Allure report and upload it as a workflow artifact even when tests fail, which preserves screenshots and failure details for investigation.

## Design Principles

- No `Thread.sleep()` calls; synchronization uses `WebDriverWait`.
- Cucumber Expressions are used instead of regular-expression step definitions.
- Feature backgrounds remove repeated login setup.
- Hooks own browser lifecycle and failure evidence.
- Page Objects contain selectors and UI behavior, not business assertions.
- Step definitions remain small and express scenario intent.
- Thread-local drivers isolate parallel scenarios.
