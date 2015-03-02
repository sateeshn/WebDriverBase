package com.solutionstar.swaftee.tests;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.webdriverFactory.AppDriver;

public class LaunchDriverTest extends AppDriver {
	
	Logger logger = getLogger(this.getClass());

	@BeforeSuite
	public void beforeMethod()
	{
		//Set the browser name for test
		System.setProperty("webdriver.browserName", "chrome");
	}
	
	@Test ( description = "webdriver start test")
	public void launchHomeSearch()
	{
		 try {
			 WebDriver driver = getDriver();
			 driver.get("https://homesearch.com");
			 Thread.sleep(2000);
			 logger.info("Current url - "+driver.getCurrentUrl());
			 Assert.assertEquals(driver.getCurrentUrl(), "https://homesearch.com/");
//			 driver.findElements(By.cssSelector(".list-inline li")).get(0).click();
//			 Thread.sleep(2000);
//			 logger.info("Current url - "+driver.getCurrentUrl());
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured");
			Assert.fail("Exception occured");
		}  
	}
	
	@Test ( description = "csv parser test")
	public void csvParserTest() throws MyCoreExceptions
	{
		HashMap <String , String[]> csvData = getCSVDataHash("testcsv");
		Assert.assertNotNull(csvData, "CSV Data hash returned as Null");
		
		HashMap <String , String> csvColumnIndex = getCSVHeaderHash();
		Assert.assertNotNull(csvColumnIndex, "CSV Column Index hash returned as Null");
		
		for(String key : csvData.keySet())
		{
			String[] rowArray = csvData.get(key);
			logger.info("CSV Row : " + getCSVData(rowArray, csvColumnIndex.get("name")) );
		}
		
//		String[] keyArr = {"id","name","propid"};
		Integer[] keyArr= {0,1,2};
		HashMap <String , String[]> csvData_with_multikey = getCSVDataHash("testcsv", keyArr);
		Assert.assertNotNull(csvData_with_multikey, "CSV Data hash returned as Null");
		
		HashMap <String , String> csvColumnIndex_with_multikey = getCSVHeaderHash();
		Assert.assertNotNull(csvColumnIndex_with_multikey, "CSV Column Index hash returned as Null");
		
		for(String key : csvData_with_multikey.keySet())
		{
			String[] rowArray = csvData_with_multikey.get(key);
			logger.info("\n key - "+ key + "\n Row value - "+ getCSVData(rowArray, csvColumnIndex_with_multikey.get("name")) );
		}
	}
	
	@Test (description = "Driver switch test")
	public void checkMethod() throws InterruptedException, MyCoreExceptions
	{
		 System.setProperty("webdriver.secondary.browser", "chrome");
		 
		 WebDriver driver = getDriver();
		 
		 startSecondaryDriver();
		 
		 WebDriver secDriver = getSecondaryDriver();
		
		 driver.get("https://google.com");
		 Thread.sleep(2000);
		
		 secDriver.get("https://google.com");
		 Thread.sleep(2000);
		 
//		 driver.findElements(By.cssSelector(".list-inline li")).get(0).click();
//		 Thread.sleep(2000);
		 driver.findElement(By.id("gb_70")).click();
		 Thread.sleep(2000);
		 
		 driver.findElement(By.id("Email")).sendKeys("sekart91");
		 driver.findElement(By.id("Passwd")).sendKeys("");
		 driver.findElement(By.id("signIn")).click();
		 Thread.sleep(2000);		 
		 logger.info("Current url - "+driver.getCurrentUrl());
		 
		 
		 secDriver.navigate().refresh();
		 Thread.sleep(2000);
		 secDriver.findElement(By.id("lst-ib")).sendKeys("new driver");
		 logger.info("Current url - "+driver.getCurrentUrl());
		 
		 secDriver.findElement(By.id("gb_70")).click();
		 Thread.sleep(2000);
		 
		 Thread.sleep(10000);

	}
}
