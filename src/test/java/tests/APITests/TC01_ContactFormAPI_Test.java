package tests.APITests;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import util.Testname;
import util.JiraPolicy;
import util.Priority;
import util.ContactAPIValidator;

public class TC01_ContactFormAPI_Test extends TestBase.TestBase {

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API rejects invalid email formats", groups = "Smoke")
    @Testname("TC001_API_01")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIInvalidEmailValidation() throws IOException, InterruptedException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        String[] invalidEmails = {
            "notanemail",
            "user",
        };
        
        
        for (String invalidEmail : invalidEmails) {
            
            java.util.Map<String, Object> formData = new java.util.HashMap<>();
            formData.put("fullName", "Test User Ahmad");
            formData.put("email", invalidEmail);
            formData.put("phoneNumber", "0551234567");
            formData.put("nationalId", "1234567890");
            formData.put("iban", "SA1234567890123456789012");
            formData.put("message", "This is a test message with more than 50 characters to meet the minimum requirement.");
            formData.put("subject", "Inquiry");
            formData.put("category", "Account");
            formData.put("subCategory", "Account Opening");
            formData.put("privacyPolicyConsent", true);
            
            io.restassured.response.Response response = apiValidator.submitContactForm(formData, recaptchaToken);
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();
            
            if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
                Assert.fail("BUG FOUND: API accepted invalid email '" + invalidEmail + 
                    "' with status " + statusCode + ". Server-side email validation is missing!");
            } else if (statusCode == 400 || statusCode == 422) {
                System.out.println("API correctly rejected invalid email '" + invalidEmail + "' with status " + statusCode);
            } else if (statusCode == 500) {
                System.err.println("BUG: API returned 500 (server error) for invalid email '" + invalidEmail + "'");
                System.err.println("    Expected: 400 or 422 (validation error)");
                System.err.println("    This indicates poor error handling - invalid input should return 4xx, not crash the server");
                System.err.println("    Response: " + responseBody);
                Assert.fail("BUG: API returned 500 instead of proper validation error (400/422) for invalid email: " + invalidEmail);
            } else {
                Assert.fail("API returned unexpected status code " + statusCode + " for invalid email: " + invalidEmail + 
                    "\nResponse: " + responseBody);
            }
            
            Thread.sleep(1000);
        }
        
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API rejects invalid phone numbers", groups = "Smoke")
    @Testname("TC001_API_02")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIInvalidPhoneValidation() throws IOException, InterruptedException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        String[] invalidPhones = {
            "123",           // Too short
            "abcdefghij",    // Letters
            "050-123-4567",  // Wrong format
            "12345"          // Too short
        };
        
        for (String invalidPhone : invalidPhones) {
            apiValidator.testInvalidPhone(invalidPhone, recaptchaToken);
            Thread.sleep(1000); // Wait between submissions
        }
        
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API validates message length (min 50 chars)", groups = "Smoke")
    @Testname("TC001_API_03")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIMessageLengthValidation() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        apiValidator.testShortMessage(recaptchaToken);
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API validates name length (min 3 chars)", groups = "Smoke")
    @Testname("TC001_API_04")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPINameLengthValidation() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        try {
            apiValidator.testShortName(recaptchaToken);
        } catch (Exception e) {
            Assert.fail("API failed to validate name length\n" + e.getMessage());
        }
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API accepts valid contact form submission", groups = "Smoke")
    @Testname("TC001_API_05")
    @Priority("Critical")
    @Severity(SeverityLevel.BLOCKER)
    public void testAPIValidFormSubmission() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        java.util.Map<String, Object> validData = new java.util.HashMap<>();
        validData.put("fullName", "Test User Ahmad");
        validData.put("email", "testuser@example.com");
        validData.put("phoneNumber", "0551234567");
        validData.put("nationalId", "1234567890");
        validData.put("iban", "SA1234567890123456789012");
        validData.put("message", "This is a test message with more than 50 characters to meet the minimum requirement.");
        validData.put("subject", "Inquiry");
        validData.put("category", "Account");
        validData.put("subCategory", "Account Opening");
        validData.put("privacyPolicyConsent", true);
        
        io.restassured.response.Response response = apiValidator.submitContactForm(validData, recaptchaToken);
        int statusCode = response.getStatusCode();
        
        Assert.assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 204,
            "API failed to accept valid form submission. Status: " + statusCode);
        
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API response time is acceptable", groups = "Smoke")
    @Testname("TC001_API_06")
    @Priority("Medium")
    @Severity(SeverityLevel.MINOR)
    public void testAPIResponseTime() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        java.util.Map<String, Object> validData = new java.util.HashMap<>();
        validData.put("fullName", "Test User Ahmad");
        validData.put("email", "testuser@example.com");
        validData.put("phoneNumber", "0551234567");
        validData.put("nationalId", "1234567890");
        validData.put("iban", "SA1234567890123456789012");
        validData.put("message", "This is a test message with more than 50 characters to meet the minimum requirement.");
        validData.put("subject", "Inquiry");
        validData.put("category", "Account");
        validData.put("subCategory", "Account Opening");
        validData.put("privacyPolicyConsent", true);
        
        apiValidator.submitContactForm(validData, recaptchaToken);
        
        long responseTime = apiValidator.getResponseTime();
        
        Assert.assertTrue(responseTime < 5000, 
            "API response time too slow: " + responseTime + "ms (expected < 5000ms)");
        
        if (responseTime > 2000) {
        } else {
        }
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API returns proper error structure for validation failures", groups = "Smoke")
    @Testname("TC001_API_07")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIErrorResponseStructure() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        java.util.Map<String, Object> emptyData = new java.util.HashMap<>();
        emptyData.put("fullName", "");
        emptyData.put("email", "");
        emptyData.put("phoneNumber", "");
        emptyData.put("nationalId", "");
        emptyData.put("iban", "");
        emptyData.put("message", "");
        emptyData.put("subject", "");
        emptyData.put("category", "");
        emptyData.put("privacyPolicyConsent", false);
        
        apiValidator.submitContactForm(emptyData, recaptchaToken);
        
        String responseBody = apiValidator.getResponseBody();
        
        Assert.assertFalse(responseBody.isEmpty(), "Error response body is empty");
        
        boolean hasErrorStructure = 
            responseBody.toLowerCase().contains("error") ||
            responseBody.toLowerCase().contains("validation") ||
            responseBody.toLowerCase().contains("invalid");
        
        Assert.assertTrue(hasErrorStructure, 
            "Error response does not contain expected error structure");
        
    }

    @JiraPolicy(logTicketReady = false)
    @Test(description = "Verify API rejects empty form submission", groups = "Smoke")
    @Testname("TC001_API_08")
    @Priority("High")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIEmptyFormRejection() throws IOException {
        
        pages.P01_Portal portal = new pages.P01_Portal(drivers.DriverHolder.getDriver());
        pages.P02_SupportPage supportPage = portal.navigateToSupportPage();
        String recaptchaToken = supportPage.captureRecaptchaToken();
        Assert.assertNotNull(recaptchaToken, "Failed to capture reCAPTCHA token from browser");
        
        ContactAPIValidator apiValidator = new ContactAPIValidator();
        
        java.util.Map<String, Object> emptyData = new java.util.HashMap<>();
        emptyData.put("fullName", "");
        emptyData.put("email", "");
        emptyData.put("phoneNumber", "");
        emptyData.put("nationalId", "");
        emptyData.put("iban", "");
        emptyData.put("message", "");
        emptyData.put("subject", "");
        emptyData.put("category", "");
        emptyData.put("privacyPolicyConsent", false);
        
        apiValidator.submitContactForm(emptyData, recaptchaToken);
        
        
        int statusCode = apiValidator.getStatusCode();
        Assert.assertTrue(statusCode >= 400 && statusCode < 500,
            "Expected 4xx status code for empty form, got: " + statusCode);
    }
}
