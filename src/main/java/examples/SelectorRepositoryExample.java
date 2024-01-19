package examples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import util.SelectorRepository;

public class SelectorRepositoryExample {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        
        try {
            
            String nameXpath = SelectorRepository.get("contactForm.fullName.xpath");
            System.out.println("Full Name XPath: " + nameXpath);
            
            String contactUrl = SelectorRepository.get("urls.contactUsUrlPart");
            System.out.println("Contact URL: " + contactUrl);
            
            
            
            By nameLocator = SelectorRepository.getByXpath("contactForm.fullName.xpath");
            
            By errorCss = SelectorRepository.getByCss("messages.errorMessage.css");
            
            By recaptchaName = SelectorRepository.getByName("recaptcha.responseTextarea.name");
            
            
            
            String optionText = "Inquiry";
            By optionLocator = SelectorRepository.getByXpath(
                "contactForm.dropdownOptions.optionByExactText", 
                optionText
            );
            System.out.println("Option Locator: " + optionLocator);
            
            
            
            driver.get("https://portal.example.com/contact-us");
            
            WebElement nameField = SelectorRepository.findElementByXpath(
                driver, 
                "contactForm.fullName.xpath"
            );
            nameField.sendKeys("Test User");
            
            WebElement option = SelectorRepository.findElementByXpath(
                driver,
                "contactForm.dropdownOptions.optionByExactText",
                "Account"
            );
            option.click();
            
            
            
            String baseUrl = SelectorRepository.get("urls.portalBaseUrl");
            String apiEndpoint = SelectorRepository.get("urls.contactApiEndpoint");
            String fullApiUrl = baseUrl + apiEndpoint;
            System.out.println("Full API URL: " + fullApiUrl);
            
            
            
            
            SelectorRepository.reload();
            System.out.println("Selectors reloaded!");
            
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    
    
    public static class ExamplePageObject {
        private WebDriver driver;
        
        public ExamplePageObject(WebDriver driver) {
            this.driver = driver;
        }
        
        public void fillNameGood(String name) {
            WebElement nameField = SelectorRepository.findElementByXpath(
                driver, 
                "contactForm.fullName.xpath"
            );
            nameField.sendKeys(name);
        }
        
        public void fillNameBad(String name) {
            WebElement nameField = driver.findElement(
                By.xpath("//input[@data-testid='fullName']")
            );
            nameField.sendKeys(name);
        }
        
        public void selectOptionGood(String optionText) {
            WebElement option = SelectorRepository.findElementByXpath(
                driver,
                "contactForm.dropdownOptions.optionByExactText",
                optionText
            );
            option.click();
        }
        
        public void selectOptionBad(String optionText) {
            WebElement option = driver.findElement(
                By.xpath("//div[contains(@class, 'option') and text()='" + optionText + "']")
            );
            option.click();
        }
    }
}
