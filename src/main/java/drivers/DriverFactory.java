package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final Duration DEFAULT_IMPLICIT_WAIT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

    public static WebDriver getNewInstance(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome-headless":
            	System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/chromedriver-mac-arm64/chromedriver"); 
                
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("window-size=1920,1080");
                
                return new ChromeDriver(chromeOptions);
            case "firefox":
                return new FirefoxDriver();
            case "firefox-headless":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                return new FirefoxDriver(firefoxOptions);
            case "edge":
            	EdgeOptions edgeOptions = new EdgeOptions();

            	edgeOptions.addArguments("start-maximized");
            	edgeOptions.addArguments("--disable-notifications");

            	System.setProperty("webdriver.edge.driver", "C:\\Users\\Public\\chromedriver-win64\\edgedriver_win64\\msedgedriver.exe");

            	return new EdgeDriver(edgeOptions);
            default:
                chromeOptions = new ChromeOptions();
                
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.default_content_setting_values.notifications", 2);
                chromeOptions.addArguments("test-type");
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--allow-running-insecure-content");
                chromeOptions.addArguments("start-maximized");
                chromeOptions.addArguments("--incognito");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                chromeOptions.merge(capabilities);

                return new ChromeDriver(chromeOptions);
        }
    }
}
