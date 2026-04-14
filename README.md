# Automation Framework

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.38-43B02A?logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/TestNG-7.9-red?logo=testng&logoColor=white)
![Allure](https://img.shields.io/badge/Allure-2.28-yellow?logo=qameta&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

A comprehensive end-to-end test automation framework built with **Java 21**, **Selenium WebDriver**, **TestNG**, and **Allure Reports**. Designed for maintainability, scalability, and seamless CI/CD integration.

---

## Table of Contents

- [Architecture](#architecture)
- [Framework Highlights](#framework-highlights)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running Tests](#running-tests)
- [Reporting](#reporting)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

---

## Architecture

The framework follows the **Page Object Model (POM)** design pattern with a centralized selector repository, thread-safe WebDriver management, and externalized configuration.

```
src/
├── main/java/
│   ├── pages/                          # Page Objects
│   │   ├── P01_Portal.java             # Portal landing page
│   │   └── P02_SupportPage.java        # Support/Contact form page
│   ├── drivers/                        # WebDriver management
│   │   ├── DriverFactory.java          # Browser initialization
│   │   └── DriverHolder.java           # Thread-safe driver storage
│   ├── util/                           # Utilities
│   │   ├── SelectorRepository.java     # Centralized element selectors
│   │   ├── ContactAPIValidator.java    # REST Assured API validator
│   │   ├── ExcelData.java              # Excel read/write operations
│   │   ├── JiraServiceProvider.java    # JIRA integration
│   │   ├── shortcut.java               # Common WebDriver actions
│   │   └── UserData.java               # Test data generation
│   ├── Listener/                       # TestNG listeners
│   │   ├── Listeners.java              # Test execution hooks
│   │   └── Retry.java                  # Failed test retry logic
│   ├── common/                         # Shared components
│   │   ├── MyReportGenerator.java      # ExtentReports setup
│   │   └── MyScreenRecorder.java       # Screenshot capture
│   ├── examples/                       # Usage examples
│   │   └── SelectorRepositoryExample.java
│   └── selenium/resources/             # Test resources
│       ├── selectors.json              # Externalized UI locators
│       ├── Data.properties             # Configuration properties
│       └── FormSubmissions.xlsx        # Test data spreadsheet
└── test/java/
    ├── TestBase/
    │   └── TestBase.java               # Base test class
    └── tests/
        ├── SmokeTests/                 # UI smoke tests
        │   ├── TC01_SupportFormValidSubmission_Test.java
        │   └── TC02_SupportFormValidationErrors_Test.java
        └── APITests/                   # API tests
            └── TC01_ContactFormAPI_Test.java
```

---

## Framework Highlights

- **Page Object Model (POM)** -- clean separation of page logic and test logic
- **Externalized Locators** -- centralized selector management via `selectors.json`
- **Data-Driven Testing** -- random data generation, Excel integration, and parameterized tests
- **Thread-Safe WebDriver** -- `ThreadLocal`-based driver management for parallel execution
- **Dual Reporting** -- Allure Reports and ExtentReports
- **JIRA Integration** -- automatic bug ticket creation on test failure
- **API Testing** -- REST Assured for backend API validation
- **CI/CD Ready** -- pre-configured YAML/XML suites for Jenkins, GitHub Actions, and Azure DevOps
- **Smart Retry** -- configurable retry mechanism for flaky tests

---

## Key Features

### Externalized Locators (Selector Repository Pattern)

All UI element locators are stored in `selectors.json` and loaded at runtime. No code changes are needed when selectors change.

```java
By nameField = SelectorRepository.getByXpath("contactForm.fullName.xpath");
String selector = SelectorRepository.get("portal.supportLink.css");
```

### Thread-Safe WebDriver Management

`DriverHolder` uses `ThreadLocal` for parallel test execution. `DriverFactory` supports Chrome, Firefox, and Edge in both headed and headless modes.

### JIRA Integration

Annotate tests with `@JiraPolicy(logTicketReady = true)` to automatically create JIRA tickets on failure, complete with screenshots and stack traces.

```java
@JiraPolicy(logTicketReady = true)
@Priority("Critical")
@Test
public void testContactFormSubmission() {
    // Auto-creates JIRA ticket on failure
}
```

### API + UI Testing

REST Assured validates API endpoints alongside Selenium-based UI tests, providing comprehensive coverage of both frontend and backend.

---

## Technology Stack

| Component | Technology | Version |
|---|---|---|
| **Language** | Java | 21 |
| **Build Tool** | Maven | 3.9+ |
| **Testing Framework** | TestNG | 7.9.0 |
| **Browser Automation** | Selenium WebDriver | 4.38.0 |
| **API Testing** | REST Assured | 5.5.0 |
| **Reporting** | Allure | 2.28.0 |
| **Reporting** | ExtentReports | 5.1.1 |
| **Data Handling** | Apache POI | 5.4.0 |
| **JIRA Integration** | Apache HttpClient | 5.2.1 |
| **JSON Parsing** | Gson | 2.11.0 |
| **Logging** | Log4j | 2.24.3 |
| **AOP** | AspectJ | 1.9.20.1 |

---

## Prerequisites

- **Java 21** (or later)
- **Maven 3.9+**
- **Git**
- A modern browser (Chrome, Firefox, or Edge)

> Selenium Manager handles browser driver downloads automatically -- no manual driver setup is required.

---

## Setup & Installation

```bash
# Clone the repository
git clone https://github.com/R-Alothaim/Automation-Framework.git
cd Automation-Framework

# Install dependencies
mvn clean install -DskipTests
```

---

## Running Tests

### Run the Default Suite

```bash
mvn clean test
```

### Run a Specific Test Suite

```bash
mvn test -DsuiteXmlFile=Chrome/Smoke-test/TC01.xml
mvn test -DsuiteXmlFile=Chrome/Smoke-test/TC02.xml
mvn test -DsuiteXmlFile=Chrome/API-tests/TC01.xml
```

### Run with Browser Selection

```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=chrome-headless
mvn test -Dbrowser=firefox
```

### Run by Test Group

```bash
mvn test -Dgroups=Smoke
mvn test -Dgroups=API
```

### Parallel Execution

```bash
mvn test -DsuiteXmlFile=testng.xml -DthreadCount=4
```

---

## Reporting

### Allure Reports

```bash
# Generate report from results
mvn allure:report

# Serve report in browser
mvn allure:serve
```

### ExtentReports

```bash
open target/test-output/ExtentReport.html
```

### TestNG Reports

```bash
open test-output/index.html
```

---

## Configuration

### `Data.properties`

Externalized configuration for application URLs, browser settings, timeouts, JIRA credentials, and feature toggles.

**Location:** `src/main/java/selenium/resources/Data.properties`

### `selectors.json`

All UI element locators in JSON format. Supports multiple locator strategies (XPath, CSS, ID, Name) per element.

**Location:** `src/main/java/selenium/resources/selectors.json`

### Test Suite Configurations

- `Chrome/Smoke-test/*.xml` -- local execution (headed browser)
- `Chrome/Smoke-test/*.yml` -- CI/CD execution (headless browser)
- `Chrome/API-tests/*.xml` -- API test suites (local)
- `Chrome/API-tests/*.yml` -- API test suites (CI/CD)

### Environment Variables

Override settings for different environments (dev, staging, production) using environment variables such as `BASE_URL`, `BROWSER`, and `API_ENDPOINT`.

---

## Project Structure

```
Automation-Framework/
├── Chrome/                         # TestNG suite configurations
│   ├── Smoke-test/                 # Smoke test suites (XML + YAML)
│   └── API-tests/                  # API test suites (XML + YAML)
├── src/
│   ├── main/java/
│   │   ├── pages/                  # Page Object classes
│   │   ├── drivers/                # WebDriver factory and holder
│   │   ├── util/                   # Utilities and helpers
│   │   ├── Listener/               # TestNG listeners
│   │   ├── common/                 # Shared components
│   │   ├── examples/               # Usage examples
│   │   └── selenium/resources/     # Config, selectors, test data
│   └── test/
│       ├── java/
│       │   ├── TestBase/           # Base test class
│       │   └── tests/              # Test classes
│       │       ├── SmokeTests/     # UI smoke tests
│       │       └── APITests/       # API validation tests
│       └── resources/
│           └── allure.properties   # Allure report configuration
├── pom.xml                         # Maven build configuration
├── .editorconfig                   # Editor formatting rules
├── .gitattributes                  # Git line-ending rules
├── .gitignore                      # Git ignore rules
├── LICENSE                         # MIT License
└── README.md                       # This file
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add your feature"`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Author

**R-Alothaim** -- [GitHub](https://github.com/R-Alothaim)
