package drivers;

import java.time.Duration;

import org.openqa.selenium.WebDriver;

public class DriverHolder {

    private final static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	public static WebDriver getDriver() {
        return driver.get();
        
    }

	public static void setDriver(WebDriver driver) {
    	driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        DriverHolder.driver.set(driver);
    }
    public static void after() {
    	getDriver().quit();
    	driver.remove();
    }
}
