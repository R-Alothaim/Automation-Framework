package tests.SmokeTests;

import java.io.IOException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import drivers.DriverHolder;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import util.Testname;
import util.JiraPolicy;
import util.Priority;
import Listener.Retry;

public class TC02_SupportFormValidationErrors_Test extends TestBase.TestBase{
	
@JiraPolicy(logTicketReady = false)
@Test(description = "Verify Support contact form validation errors", groups = "Smoke" , retryAnalyzer = Retry.class)
@Testname("TC002")
@Priority("High")
@Severity(SeverityLevel.NORMAL)
public void testFormValidationErrors() throws IOException  {
    
    pages.P02_SupportPage supportPage = new pages.P01_Portal(DriverHolder.getDriver())
        .navigateToSupportPage()
        .clickSend();
    
    List<String> errors = supportPage.getValidationErrors();
    
    boolean hasNameError = errors.stream().anyMatch(e -> e.contains("Name") && e.contains("3 characters"));
    boolean hasNationalIdError = errors.stream().anyMatch(e -> e.contains("National Id"));
    boolean hasPhoneError = errors.stream().anyMatch(e -> e.contains("Phone number"));
    boolean hasEmailError = errors.stream().anyMatch(e -> e.contains("Email"));
    boolean hasIBANError = errors.stream().anyMatch(e -> e.contains("IBAN"));
    boolean hasMessageError = errors.stream().anyMatch(e -> e.contains("message") && e.contains("50 characters"));
    boolean hasPrivacyError = errors.stream().anyMatch(e -> e.contains("Privacy Notice"));
    
    List<String> missingErrors = new java.util.ArrayList<>();
    if (!hasNameError) missingErrors.add("Name (must be at least 3 characters)");
    if (!hasNationalIdError) missingErrors.add("National Id (required)");
    if (!hasPhoneError) missingErrors.add("Phone number (required)");
    if (!hasEmailError) missingErrors.add("Email (required)");
    if (!hasIBANError) missingErrors.add("IBAN (required)");
    if (!hasMessageError) missingErrors.add("Message (must be at least 50 characters)");
    if (!hasPrivacyError) missingErrors.add("Privacy Notice (must be accepted)");
    
    if (!missingErrors.isEmpty()) {
        Assert.fail("Expected validation errors NOT displayed:\n" +
                    "Missing: " + missingErrors );
    }
    
    
   
}

}
