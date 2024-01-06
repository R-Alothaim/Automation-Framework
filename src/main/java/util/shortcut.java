package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class shortcut  {

public static WebDriver driver;
public static Properties prop = new Properties();

public static void data() throws IOException {
	FileInputStream fis;

	fis = new FileInputStream(System.getProperty("user.dir")+"/src/main/java/selenium/resources/Data.properties");
	prop.load(fis);
}

	public void waitforvis(By findBy) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));

		
	}
	public void waitforinv(By findBy) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.invisibilityOf(driver.findElement(findBy)));

		
	}
	public WebElement streem(String produ, By findBy) {
		List<WebElement> products = driver.findElements(By.cssSelector(".card-body"));
		return products.stream().filter(product -> product.findElement(findBy).getText().equals(produ)).findFirst().orElse(null);

	}
	
	public boolean matching(List<WebElement> x, String m) {
		boolean match = x.stream().anyMatch(cartProduct->cartProduct.getText().equalsIgnoreCase(m));
		return match;
		
	}
	
	public void list(String cssselector,int nth,String key) {
		List<WebElement>list = driver.findElements(By.cssSelector(cssselector));
		list.get(nth).sendKeys(key);
	}
	public WebElement css(WebDriver driver,String css) {
		return driver.findElement(By.cssSelector(css));
		
	}
	public WebElement xpath(WebDriver driver,String xpath) {
		return driver.findElement(By.xpath(xpath));
		
	}
	public WebElement id(WebDriver driver,String id) {
		
		return driver.findElement(By.id(id));
		
	}
}
