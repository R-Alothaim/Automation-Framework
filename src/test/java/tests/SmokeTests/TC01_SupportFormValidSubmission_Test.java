package tests.SmokeTests;

import java.io.IOException;

import org.testng.annotations.Test;

import Listener.Retry;

import drivers.DriverHolder;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import util.Testname;
import util.JiraPolicy;
import util.Priority;

public class TC01_SupportFormValidSubmission_Test extends TestBase.TestBase{

@JiraPolicy(logTicketReady = false)
@Test(description = "Verify Support contact form submission with valid data", groups = "Smoke", retryAnalyzer = Retry.class)
@Testname("TC001")
@Priority("Highest")
@Severity(SeverityLevel.CRITICAL)

public void submitSupportFormWithValidData() throws IOException  {
    
    String issueType = getRandomIssueType();
    String category = getRandomCategory();
    String subCategory = getRandomSubCategory(category);
    
    
    pages.P02_SupportPage supportPage = new pages.P01_Portal(DriverHolder.getDriver())
        .navigateToSupportPage()
        .fillName(generate("Characters",10))
        .fillEmail(generate("Email",1))
        .fillNationalId("12" + generate("Numbers",8))
        .fillPhone("5" + generate("Numbers",8))
        .fillIBAN("SA" + generate("Numbers",22))
        .fillMessage("Test inquiry: " + generate("Characters_And_Numbers",50))
        .selectIssueType(issueType)
        .selectCategory(category)
        .selectSubCategory(subCategory)
        .acceptPrivacyNotice()
        .clickSend();
    
    String successMessage = supportPage.getSuccessMessage();
    
    org.testng.Assert.assertTrue(
        successMessage.contains("Your message has been sent successfully") || 
        successMessage.contains("Thank you for reaching out") ||
        successMessage.contains("تم إرسال رسالتك بنجاح"),
        "Success message not found. Actual message: " + successMessage);
    
    
    boolean excelValidated = supportPage.validateExcelRecording();
    org.testng.Assert.assertTrue(excelValidated, "Excel recording validation failed - data mismatch!");
    
}

}
