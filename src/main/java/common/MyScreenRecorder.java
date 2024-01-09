package common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import drivers.DriverHolder;

public class MyScreenRecorder extends DriverHolder{
    public static String getScreenshot(String string) throws IOException {
    	TakesScreenshot ts=(TakesScreenshot)DriverHolder.getDriver();
    	File source=ts.getScreenshotAs(OutputType.FILE);
    	File file = new File(System.getProperty("user.dir")+"//reports//"+string+".png");
    	FileUtils.copyFile(source, file);
    	return System.getProperty("user.dir")+"//reports//"+string+".png";
    	
    }


}