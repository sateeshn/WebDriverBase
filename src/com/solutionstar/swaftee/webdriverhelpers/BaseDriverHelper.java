package com.solutionstar.swaftee.webdriverhelpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.browserlaunchers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solutionstar.swaftee.CustomExceptions.MyCoreExceptions;
import com.solutionstar.swaftee.config.WebDriverConfig;
import com.solutionstar.swaftee.constants.WebDriverConstants;

import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseDriverHelper {
	
	WebDriver driver = null;
	WebDriver secondaryDriver = null;	
	ProxyServer proxyServer = null;
	
	Logger logger = getLogger(this.getClass());
	
	enum DriverTypes
	{
		   primary, secondary
    } 
	
	enum BrowserNames
	{
		   chrome, firefox,ie,phantomjs
    } 
	
	public void startServer() throws InterruptedException
	   {
			if(proxyServer !=  null)
				return;
		  	proxyServer = new ProxyServer(0); //port number equals to zero starts the server in dynamic port
		  	try {
	       	proxyServer.start();
	       
	      // Start the server in specified host and port - TODO
//	      Map<String, String> options = new HashMap<String, String>();
//	      options.put("httpProxy", "127.0.0.1" + ":" + "3000");
//	      proxyServer.setOptions(options);
	       	
	       } catch (Exception e) {
	         String error = "Error while starting server.. " + e.getStackTrace();
	         logger.error(error);
//	         logger.error(error);
	       }
	   }
	
	   public void startDriver() throws Exception
	   {
		 	    if(driver != null)
			    	return;
			    String browserName = getBrowserName("primary");
			    logger.info("browserName -- "+ browserName);
			    DesiredCapabilities cap = createDriverCapabilities(browserName);		
				if (cap == null)
					throw new MyCoreExceptions("Capabilities return as Null");
				
				driver = setWebDriver(cap);
		 
		}

	   public WebDriver setWebDriver(DesiredCapabilities cap2) throws Exception
	   {
		   if(WebDriverConfig.usingGrid())
			{
			   cap2 = setRemoteDriverCapabilities(cap2.getBrowserName());
				driver = setRemoteWebDriver(cap2);
			}
			else
				driver = startBrowser(cap2);
		    createProxy(cap2);
			return driver;
	   }
	   
	   
	   public void startSecondaryDriver() throws Exception
	   {
	  	    if(secondaryDriver != null)
		    	return;			    
		    String browserName = getBrowserName("secondary");
		    DesiredCapabilities cap = createDriverCapabilities(browserName);		
			if (cap == null)
				throw new MyCoreExceptions("Capabilities return as Null");
			logger.info("browserName -- "+ browserName);
			secondaryDriver = setWebDriver(cap);
				
	   }
	     
	   private String getBrowserName(String driverTypeStr) throws MyCoreExceptions
	   {
		   String browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;
		   try{
			    switch(DriverTypes.valueOf(driverTypeStr))
			    {
				    case primary   : 	browserName = System.getProperty("webdriver.browser", WebDriverConstants.DEFAULT_BROWSER_NAME) ;  // Setting the default browser if no browser name is specified
				    				   	break;
				    case secondary : 	browserName = System.getProperty("webdriver.secondary.browser", WebDriverConstants.DEFAULT_BROWSER_NAME) ;
					 				 	break;
				    default			 : 	browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;						
			    }
				browserName = browserName.toLowerCase() ;
				browserName =  WebDriverConstants.DRIVER_METHOD.containsKey(browserName) ? browserName : WebDriverConstants.DEFAULT_BROWSER_NAME;					
		   }catch(Exception e){
			   throw new MyCoreExceptions("Exception while assiging browserName");
		   }
			return browserName;
	   }
	   
	   private DesiredCapabilities createDriverCapabilities(String browserName)
	   {
		   DesiredCapabilities cap = null;			
		   try{
			    SetBrowserCapabilities setBrowserCapabilities = new SetBrowserCapabilities();
				Method setCapabilities = setBrowserCapabilities.getClass().getMethod(WebDriverConstants.DRIVER_METHOD.get(browserName),DesiredCapabilities.class);
				cap = (DesiredCapabilities) setCapabilities.invoke(setBrowserCapabilities, cap);

		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return cap;
	   }
	   
	   private WebDriver startBrowser(DesiredCapabilities cap)
	   {  
		   WebDriver driver = null;
		   try{
				switch (BrowserNames.valueOf(cap.getBrowserName())) 
			    {
				     case chrome:
				    	driver = new ChromeDriver(cap);
			   			break;
					case ie:
						driver = new InternetExplorerDriver();
			   			break;
					case firefox:
						driver = new FirefoxDriver(cap);
			   			break;
					case phantomjs:
						driver = new PhantomJSDriver(cap);
			   			break;
					default:
			            throw new IllegalArgumentException("Invalid Argument for browser name : " + cap.getBrowserName());
			    }
				
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return driver;
	   }
	   
	 
	   private DesiredCapabilities setRemoteDriverCapabilities(String browserName) throws Exception
	   {
		 	DesiredCapabilities capab = new DesiredCapabilities();
			capab.setBrowserName(browserName);
			if(System.getProperty("webDriver.browser.version") != null)
				capab.setVersion(System.getProperty("webDriver.browser.version"));
			capab.setPlatform(getOperatingSystem());
			
			return capab;
	   }
	
	   private Platform getOperatingSystem() 
	   {
			String os = System.getProperty("webDriver.platform.OS",WebDriverConstants.DEFAULT_BROWSER_OS);
			switch(OperatingSystem.valueOf(os.toLowerCase()))
			{
			case windows:
				return Platform.WINDOWS;
			case mac:
				return Platform.MAC;
			}
			return null;
	   }
	   enum OperatingSystem
	   {
		   windows,mac
	   }
	   private DesiredCapabilities setDriverCapabilities(String browserName) throws Exception
	   {
		   DesiredCapabilities cap = null;
		   SetBrowserCapabilities setBrowserCapabilities = new SetBrowserCapabilities();
		   browserName =  WebDriverConstants.DRIVER_METHOD.containsKey(browserName) ? browserName : "chrome";
	
		   Method setCapabilities = setBrowserCapabilities.getClass().getMethod(WebDriverConstants.DRIVER_METHOD.get(browserName),DesiredCapabilities.class);
		   return (DesiredCapabilities) setCapabilities.invoke(setBrowserCapabilities, cap);
	   }
	   
	   private WebDriver setRemoteWebDriver(DesiredCapabilities cap) throws Exception
	   {
			return new RemoteWebDriver(new URL("http://"+ WebDriverConfig.getWebDriverProperty("gridserver") +":"+ WebDriverConfig.getWebDriverProperty("gridserverport") +"/wd/hub"),cap);
	   }   
	   
	   
	    private Proxy createProxyObject() 
	    {
	    	Proxy proxy = null;
	    	try {
	    		proxy = proxyServer.seleniumProxy();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        // set server properties.
	        proxyServer.setCaptureHeaders(true);// capture headers
	        proxyServer.setCaptureContent(true);// capture content.

	        return proxy;
	      }
	    
	    private void createProxy(DesiredCapabilities cap)
	    {
	    	try{
	    		Proxy proxy = createProxyObject();
				if (proxy != null)
				      cap.setCapability(CapabilityType.PROXY, proxy);
				else
					logger.info("Proxy object is null");
		    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    public void stopServer()
	    {
	    	try{
	    		proxyServer.stop();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	    
	    public void stopDriver() 
	    {
	    	 if(getDriver() != null) 
	 	    {
	 	      getDriver().quit();
	 	      setDriver(null);
	 	    }
	 	    if(getSecondaryDriver() != null)
	 	    {
	 	      getSecondaryDriver().quit();
	 	      setSecondaryDriver(null);	    	
	 	    }
	    }
	    
	    public WebDriver getDriver()
	    {
	    	return this.driver;
	    }
	    
	    public WebDriver getSecondaryDriver()
	    {
	    	return this.secondaryDriver;
	    }
	    
	    public void setDriver(Object obj)
	    {
	    	this.driver = (WebDriver) obj;
	    }
	    
	    public void setSecondaryDriver(Object obj)
	    {
	    	this.secondaryDriver = (WebDriver) obj;
	    }
	    
	    private void printCapabilities(Capabilities capabilities)
	    {
	        Map<String, ?> map = capabilities.asMap();
	        for (Entry<String, ?> entry : map.entrySet()) {
	          String key = entry.getKey();
	          Object value = entry.getValue();
	          logger.info("\t\tkey is " + key + "\t\tvalue is " + value);
	        }
	    }

		public Logger getLogger(Class<?> className) 
		{
			Logger logger = null;
			try{
				logger = LoggerFactory.getLogger(className);				
			}catch(Exception e){
				e.printStackTrace();
			}
			return logger;
		}	
		
		public String getPrimaryWinhandle() throws MyCoreExceptions
		{
			try{
//				if(this.driver == null)
//					throw new MyCoreExceptions("Unable to get the winhandle as the driver is set as null");
				return this.driver.getWindowHandle();
			}catch(Exception e){
				e.printStackTrace();
				throw new MyCoreExceptions("Exception occured... "+ e.getStackTrace());
				
			}
		}
		
		public String getSecondaryWinhandle() throws MyCoreExceptions
		{
			try{
				if(this.secondaryDriver == null)
					throw new MyCoreExceptions("Unable to get the winhandle as the driver is set as null");
				return this.secondaryDriver.getWindowHandle();
			}catch(Exception e){
				throw new MyCoreExceptions("Exception occured... "+ e.getStackTrace());
			}
		}
}
