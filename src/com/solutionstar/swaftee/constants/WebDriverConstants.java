package com.solutionstar.swaftee.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebDriverConstants {
	
	public static String PATH_TO_BROWSER_EXECUTABLE = "\\resources\\drivers\\";
	public static String PATH_TO_BROWSER_SCREENSHOT = "resources\\screenshot\\";
	public static final Map<String, String> DRIVER_METHOD;
	static {
	  Map<String, String> tmp = new LinkedHashMap<String, String>();
	  tmp.put("ie", "setIEDriver");
	  tmp.put("firefox", "setFirefoxDriver");
	  tmp.put("chrome", "setChromeDriver");
	  tmp.put("phantomjs", "setPhomtomJsDriver");
	  DRIVER_METHOD = Collections.unmodifiableMap(tmp);
	}
	public static String DEFAULT_BROWSER_NAME = "chrome";
	public static int WAIT_FOR_VISIBILITY_TIMEOUT_IN_SEC = 10;
	public static int MAX_TIMEOUT_PAGE_LOAD = 40;
	final public static String PROXY_SERVER  = "proxyserver.enabled";
	final public static String GRID_SERVER  = "grid.enabled";
	public static final String DEFAULT_BROWSER_OS = "windows";

}
