package Listener;

import java.io.FileInputStream;
import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import common.MyReportGenerator;
import common.MyScreenRecorder;
import io.qameta.allure.Allure;
import util.JiraPolicy;
import util.Testname;
import util.JiraServiceProvider;
import util.Priority;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Listeners implements ITestListener{
	ExtentTest test;

	ExtentReports extent = MyReportGenerator.getReport();
	ThreadLocal<ExtentTest> extentTest=new ThreadLocal<ExtentTest>();
	@Override
	public void onTestStart(ITestResult result) {

		test = extent.createTest(result.getMethod().getMethodName());
		extentTest.set(test);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
	    extentTest.get().log(Status.PASS, "Test Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
	    
			extentTest.get().fail(result.getThrowable());

		
	    String filepath = null;
	    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String methodName = result.getMethod().getMethodName();

	    String screenshotName = methodName + "_" + timestamp + ".png";

	    try {
	        filepath = MyScreenRecorder.getScreenshot(screenshotName);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    JiraPolicy jiraPolicy = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(JiraPolicy.class);
	    Testname testName  = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Testname.class);
	    Priority prioritY  = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Priority.class);

	    boolean isTicketReady = jiraPolicy.logTicketReady();
	    
	    if (isTicketReady) {
	        String testname = testName.value();
	        String priority = prioritY.value();
	        
	    	JiraServiceProvider jiraSp = new JiraServiceProvider("http://localhost:2990/jira",
	                "admin", "admin");

	        String issueSummary = "[" + testname + "] Failure in " + 
	                              result.getMethod().getConstructorOrMethod().getMethod().getDeclaringClass() + 
	                              ", specifically in '" + result.getMethod().getDescription() + "' due to assertion or exception";
	        String issueDescription = result.getThrowable().getMessage() + "\n";
	        

	        String issueKey = jiraSp.createIssue("NC", "Bug", issueSummary, 
	                          issueDescription.replace("\\", "").replace("\n", "\\n").replace("\"", ""),priority, filepath);

	        if (issueKey != null && filepath != null) {
	            jiraSp.addAttachment(issueKey, filepath, jiraSp.getHttpClient());
	        }
	        
	    }
	    if (filepath != null) {
            try (FileInputStream fis = new FileInputStream(filepath)) {
                Allure.addAttachment(result.getMethod().getMethodName(), fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	    extentTest.get().addScreenCaptureFromPath(filepath, screenshotName);
	    ITestListener.super.onTestFailure(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
	    extentTest.get().log(Status.SKIP, "Test Skipped");
		ITestListener.super.onTestSkipped(result);

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
		ITestListener.super.onTestFailedWithTimeout(result);
	}

	@Override
	public void onStart(ITestContext context) {
		ITestListener.super.onStart(context);
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
		ITestListener.super.onFinish(context);
	}

}
