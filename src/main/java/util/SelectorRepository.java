package util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SelectorRepository {
    
    private static JsonObject selectors;
    private static final String SELECTORS_FILE_PATH = System.getProperty("user.dir") + "/src/main/java/selenium/resources/selectors.json";
    
    static {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SELECTORS_FILE_PATH)));
            Gson gson = new Gson();
            selectors = gson.fromJson(content, JsonObject.class);
            System.out.println("Selectors loaded successfully from: " + SELECTORS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Failed to load selectors from: " + SELECTORS_FILE_PATH);
            e.printStackTrace();
            throw new RuntimeException("Cannot initialize SelectorRepository - selectors.json not found", e);
        }
    }
    
    public static String get(String path) {
        try {
            String[] keys = path.split("\\.");
            JsonElement element = selectors;
            
            for (String key : keys) {
                if (element.isJsonObject()) {
                    element = element.getAsJsonObject().get(key);
                } else {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
            }
            
            if (element == null) {
                throw new IllegalArgumentException("Selector not found: " + path);
            }
            
            return element.getAsString();
        } catch (Exception e) {
            System.err.println("Error getting selector: " + path);
            throw new RuntimeException("Selector not found: " + path, e);
        }
    }
    
    public static String get(String path, String text) {
        String selector = get(path);
        return selector.replace("{{TEXT}}", text);
    }
    
    public static By getByXpath(String path) {
        return By.xpath(get(path));
    }
    
    public static By getByXpath(String path, String text) {
        return By.xpath(get(path, text));
    }
    
    public static By getByCss(String path) {
        return By.cssSelector(get(path));
    }
    
    public static By getByName(String path) {
        return By.name(get(path));
    }
    
    public static By getById(String path) {
        return By.id(get(path));
    }
    
    public static By getByTagName(String path) {
        return By.tagName(get(path));
    }
    
    public static WebElement findElementByXpath(WebDriver driver, String path) {
        return driver.findElement(getByXpath(path));
    }
    
    public static WebElement findElementByXpath(WebDriver driver, String path, String text) {
        return driver.findElement(getByXpath(path, text));
    }
    
    public static WebElement findElementByCss(WebDriver driver, String path) {
        return driver.findElement(getByCss(path));
    }
    
    public static WebElement findElementByName(WebDriver driver, String path) {
        return driver.findElement(getByName(path));
    }
    
    public static String getUrl(String path) {
        return get("urls." + path);
    }
    
    public static void reload() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SELECTORS_FILE_PATH)));
            Gson gson = new Gson();
            selectors = gson.fromJson(content, JsonObject.class);
            System.out.println("Selectors reloaded successfully");
        } catch (IOException e) {
            System.err.println("Failed to reload selectors");
            e.printStackTrace();
        }
    }
    
    public static JsonObject getSection(String path) {
        try {
            String[] keys = path.split("\\.");
            JsonElement element = selectors;
            
            for (String key : keys) {
                if (element.isJsonObject()) {
                    element = element.getAsJsonObject().get(key);
                } else {
                    throw new IllegalArgumentException("Invalid path: " + path);
                }
            }
            
            if (element == null || !element.isJsonObject()) {
                throw new IllegalArgumentException("Section not found or not an object: " + path);
            }
            
            return element.getAsJsonObject();
        } catch (Exception e) {
            System.err.println("Error getting section: " + path);
            throw new RuntimeException("Section not found: " + path, e);
        }
    }
    
    public static void printAllSelectors() {
        System.out.println("\n===== Available Selectors =====");
        System.out.println(new Gson().toJson(selectors));
        System.out.println("===============================\n");
    }
}
