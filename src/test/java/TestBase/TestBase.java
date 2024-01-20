package TestBase;

import drivers.DriverFactory;
import drivers.DriverHolder;
import util.shortcut;
import java.util.Random;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.IOException;

public class TestBase extends shortcut{
    @Parameters("browser")
    @BeforeMethod
    public void setupDriver(@Optional("chrome") String browser) throws IOException {
    	shortcut.data();

      WebDriver driver = DriverFactory.getNewInstance(browser);
     DriverHolder.setDriver(driver);
        

    }
    @AfterMethod
   public void tearDown() {
      DriverHolder.after();
        
    }
    @AfterSuite
    public void cleanup() throws InvalidFormatException {
     
    }

    public static String generate(String type, int length) {
        Random random = new Random();
        StringBuilder username = new StringBuilder(length);

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String allowedChars = "";

        switch (type) {
            case "Characters":
                allowedChars = characters;
                break;
            case "Numbers":
                allowedChars = numbers;
                break;
            case "Characters_And_Numbers":
                allowedChars = characters + numbers;
                break;
            case "Email":
                String emailPrefix = generate("Characters_And_Numbers", 8);
                String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "test.com"};
                return emailPrefix + "@" + domains[random.nextInt(domains.length)];
            default:
                throw new IllegalArgumentException("Invalid type. Use 'Characters', 'Numbers', 'Characters_And_Numbers', or 'Email'.");
        }

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allowedChars.length());
            username.append(allowedChars.charAt(randomIndex));
        }

        return username.toString();
    }
    
    public static String getRandomIssueType() {
        String[] issueTypes = {"Inquiry", "Products", "Complaint"};
        Random random = new Random();
        return issueTypes[random.nextInt(issueTypes.length)];
    }
    
    public static String getRandomCategory() {
        String[] categories = {"Account", "Payments", "Cards", "Lending", "Mobile App", "Transfer"};
        Random random = new Random();
        return categories[random.nextInt(categories.length)];
    }
    
    public static String getRandomSubCategory(String category) {
        Random random = new Random();
        
        switch (category) {
            case "Account":
                String[] accountOptions = {"Onboarding", "Account"};
                return accountOptions[random.nextInt(accountOptions.length)];
                
            case "Payments":
                String[] paymentOptions = {"Top-up", "SADAD", "VISA", "MADA", "GCC"};
                return paymentOptions[random.nextInt(paymentOptions.length)];
                
            case "Cards":
                return ""; // No specific options provided
                
            case "Lending":
                return "";
                
            case "Mobile App":
                String[] mobileAppOptions = {"Technical Issue", "Update information"};
                return mobileAppOptions[random.nextInt(mobileAppOptions.length)];
                
            case "Transfer":
                String[] transferOptions = {"Internal Transfer", "Local Transfer", "International transfer"};
                return transferOptions[random.nextInt(transferOptions.length)];
                
            default:
                return "";
        }
    }
    }
