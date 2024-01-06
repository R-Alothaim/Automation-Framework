package pages;


import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import util.shortcut;
import util.SelectorRepository;


public class P01_Portal extends shortcut {
	private static final int NAVIGATION_WAIT_TIMEOUT = 5;
	
	WebDriver driver;
			public P01_Portal(WebDriver driver)  {
				
			   this.driver = driver;
				driver.get(prop.getProperty("PORTAL_URL"));
				
				rejectCookies();
			
			}

	    private void rejectCookies() {
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    	try {
	    		wait.until(ExpectedConditions.elementToBeClickable(SelectorRepository.getByXpath("portal.cookieReject.xpath")));
	    		xpath(driver, SelectorRepository.get("portal.cookieReject.xpath")).click();
	    	} catch (Exception e) {
	    		try {
	    			wait.until(ExpectedConditions.elementToBeClickable(SelectorRepository.getByCss("portal.cookieReject.css")));
	    			css(driver, SelectorRepository.get("portal.cookieReject.css")).click();
	    		} catch (Exception e2) {
	    			System.out.println("No cookie banner found or already handled: " + e2.getMessage());
	    		}
	    	}
	    }

	    public P02_SupportPage navigateToSupportPage()  {
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(NAVIGATION_WAIT_TIMEOUT));	    	try {
	    		wait.until(ExpectedConditions.elementToBeClickable(SelectorRepository.getByXpath("portal.supportLink.xpath")));
		    xpath(driver, SelectorRepository.get("portal.supportLink.xpath")).click();
		} catch (Exception e) {
			try {
			    wait.until(ExpectedConditions.elementToBeClickable(SelectorRepository.getByCss("portal.supportLink.css")));
			    css(driver, SelectorRepository.get("portal.supportLink.css")).click();
				} catch (Exception e2) {
					Assert.fail("Failed to find Support link: " + e2.getMessage());
				}
			}
	    	
	    	try {
	    		wait.until(ExpectedConditions.urlContains(SelectorRepository.get("urls.contactUsUrlPart")));
			} catch (Exception e) {
				 Assert.fail("Failed to navigate to Support page. Current URL: " + driver.getCurrentUrl());
			}		        return new P02_SupportPage(driver);
		    }
		    
		    
		}
