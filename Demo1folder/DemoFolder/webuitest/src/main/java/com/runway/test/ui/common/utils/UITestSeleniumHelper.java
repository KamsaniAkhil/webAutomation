/**
 * 
 */
package com.runway.test.ui.common.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opera.core.systems.OperaDriver;

/**
 * UI common wrapper test code using Selenium WebDriver
 * 
 * Use in the Test classes.
 * 
 */
public class UITestSeleniumHelper {
	static Logger logger = LoggerFactory.getLogger(UITestSeleniumHelper.class);

	public static String APP_BASE_URL = null;
	public WebDriver driver = null;
	public final static String FF_BROWSER = "firefox";
	public final static String CR_BROWSER = "chrome";
	public final static String IE_BROWSER = "ie";
	public final static String IE_EDGE_BROWSER = "edge";
	public final static String SF_BROWSER = "safari";
	public final static String OP_BROWSER = "opera";
	public final static String HU_BROWSER = "htmlunit";
	public static boolean isWin = false;
	public static boolean isLinux = false;
	public static boolean isUnix = false;
	public static boolean isMac = false;
	public static String execExtn = "";
	public static String defaultBrowser = "chrome";
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		System.out.println("os.name=" + osName);
		if (osName.indexOf("win") != -1) {
			isWin = true;
			execExtn = ".exe";
			defaultBrowser = "ie";
		} else if (osName.indexOf("nix") != -1) {
			isUnix = true;
			defaultBrowser = "firefox";
		} else if (osName.indexOf("mac") != -1) {
			isMac = true;
			defaultBrowser = "safari";
		}
		defaultBrowser = "chrome"; // set it as default for now.
	}
	Properties props = new Properties();
	public final static String DRIVER_PATH = System.getProperty("selenium.drivers", "lib");
	public final static String FIREFOX_DRIVER_PATH = System.getProperty("webdriver.gecko.driver",
			DRIVER_PATH + File.separator + "geckodriver" + execExtn);
	public final static String CR_DRIVER_PATH = System.getProperty("webdriver.chrome.driver",
			DRIVER_PATH + File.separator + "chromedriver" + execExtn);
	public final static String IE_DRIVER_PATH = System.getProperty("webdriver.ie.driver",
			DRIVER_PATH + File.separator + "IEDriverServer" + execExtn);
	public final static String IE_EDGE_DRIVER_PATH = System.getProperty("webdriver.ie.driver",
			DRIVER_PATH + File.separator + "MicrosoftWebDriver" + execExtn);

	public final static long WAIT_TIME = Long.parseLong(System.getProperty("wait.time", "60")); // secs
	public static String TEST_TENANT = null;
	private static long TIMEOUT;
	public static String DEFAULT_USER_NAME = null;
	public static String DEFAULT_PASSWRD = null;
	public static String BOX_USER = null;
	public static String BOX_PASSWRD = null;
	private static final String TEST_PROPERTY_FILE = "test.properties";
	public static String AWS_AKEY = null;
	public static String API_HOST = null;
	public static String AWS_PKEY = null;
	public static String SF_PASSWORD = null;
	public static String SF_USER = null;
	public String browser = System.getProperty("browser", defaultBrowser);

	/*
	 * 
	 */
	public UITestSeleniumHelper() {
		// Load Test Properties
		// From File or system
		props = PropsHandler.getProperties(TEST_PROPERTY_FILE);
		APP_BASE_URL = props.getProperty("app.base.url", "http://localhost:8080/WebApp");
		TEST_TENANT = props.getProperty("tenant.name", "KeyTech");
		TIMEOUT = new Integer(props.getProperty("time.out", "25")).intValue();
		DEFAULT_USER_NAME = props.getProperty("user.name", "testuser1");
		DEFAULT_PASSWRD = props.getProperty("user.password", "testuser123");

		API_HOST = props.getProperty("api.host", "http://localhost:8080");
		logger.info("***** UITestHAndler: APP_BASE_URL " + UITestSeleniumHelper.APP_BASE_URL);
	}

	/**
	 * Open the new browser window by creating the web driver
	 * 
	 * @param browser
	 * @param driverPath
	 * @return
	 */
	public WebDriver openBrowser(String browser) {
		if (FF_BROWSER.equals(browser)) {
			logger.info("Opening " + FF_BROWSER);
			System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_PATH);
			driver = new FirefoxDriver();
			DesiredCapabilities caps = new DesiredCapabilities().firefox();
			caps.setCapability("ignoreProtectedModeSettings", true);
			caps.setCapability("ignoreZoomSetting", true);
			caps.setCapability("draggable", true);
			caps.setCapability("requireWindowFocus", true);
		} else if (CR_BROWSER.equals(browser)) {
			logger.info("Opening " + CR_BROWSER);
			System.setProperty("webdriver.chrome.driver", CR_DRIVER_PATH);
			DesiredCapabilities caps = new DesiredCapabilities().chrome();
			caps.setCapability("ignoreProtectedModeSettings", true);
			caps.setCapability("ignoreZoomSetting", true);
			caps.setCapability("draggable", true);
			caps.setCapability("requireWindowFocus", true);
			caps.setCapability("chrome.switches", Arrays.asList("--incognito"));
			driver = new ChromeDriver();
		} else if (IE_BROWSER.equals(browser)) {
			logger.info("Opening " + IE_BROWSER);
			System.setProperty("webdriver.ie.driver", IE_DRIVER_PATH);
			DesiredCapabilities caps = new DesiredCapabilities().internetExplorer();
			// caps.setCapability("ignoreProtectedModeSettings", true);
			caps.setCapability("ignoreZoomSetting", true);
			caps.setCapability("draggable", true);
			caps.setCapability("requireWindowFocus", true);
			driver = new InternetExplorerDriver();
		} else if (IE_EDGE_BROWSER.equals(browser)) {
			logger.info("Opening " + IE_EDGE_BROWSER);
			System.setProperty("webdriver.edge.driver", IE_EDGE_DRIVER_PATH);
			driver = new EdgeDriver();
		} else if (SF_BROWSER.equals(browser)) {
			logger.info("Opening " + SF_BROWSER);
			driver = new SafariDriver();
		} else if (OP_BROWSER.equals(browser)) {
			logger.info("Opening " + OP_BROWSER);
			driver = new OperaDriver();
		} else if (HU_BROWSER.equals(browser)) {
			logger.info("Opening " + HU_BROWSER);
			driver = new HtmlUnitDriver();
		} else {
			logger.info("Opening default " + FF_BROWSER);
			driver = new FirefoxDriver();
		}
		return driver;
	}

	/*
	 * opening the Diabled chrome browser while creating the Webdriver
	 */

	public WebDriver openDisabledBrowser(String browser) {

		System.setProperty("webdriver.chrome.driver", DRIVER_PATH + File.separator + "chromedriver" + execExtn);
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("test-type");
		options.addArguments("--start-maximized");
		options.addArguments("--disable-web-security");
		options.addArguments("--allow-running-insecure-content");
		capabilities.setCapability("chrome.binary", "./src//lib//chromedriver");
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(capabilities);
		return driver;

	}
    
	
	/**
	 * Quit/Exit all browser windows
	 */
	public void quitBrowser() {
		logger.info("Quitting browser...");
		driver.quit();

	}

	/* For Opening AIMS browser */
	public void setup() {
		openBrowser("chrome");
		maximizeWindow();

		logger.info("Navigating to the URL");
		navigateTo("https://dev-jr365.aims360runway.com/Reports/OtsByDate?scan=true");
		waitSimply(3);
	}

      //	Login to the application

	public void loginPage(String username,String password) {

		logger.info("Getting UserName component and send username");
		driver.findElement(By.name("userName")).sendKeys(username);

		logger.info("Getting password component and send password");
		driver.findElement(By.name("password")).sendKeys(password);

		logger.info("Getting Sign In component");
		driver.findElement(By.xpath("//button[contains(@class,'btn-success')]")).click();

	}

	/**
	 * Close the current browser tab/window
	 */
	public void closeBrowser() {
		logger.info("Closing browser...");
		driver.close();

	}

	/**
	 * Set element time wait commonly used during all elements search
	 * 
	 * @param timeWait
	 */
	public void setTimeOut(long timeWait) {
		driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);

	}

	/**
	 * Goto a particular url
	 * 
	 * @param url
	 */
	public void navigateTo(String url) {
		logger.info("Going to url - " + url);
		driver.navigate().to(url);

	}
	
	
	public void naviagateBack() {
		
		driver.navigate().back();
	}

	public void maximizeWindow() {
		logger.info("Maximize window...");
		driver.manage().window().maximize();

	}
	public void refreshBrowser() {
		driver.navigate().refresh();

	}

	/*
	 * Convenient methods for locating elements
	 */
	public WebElement findElementById(String id) {
		logger.info("Find an element by id: " + id);
		waitForElementToBeLoadedById(id);
		return driver.findElement(By.id(id));
	}

	public List<WebElement> findElementsById(String id) {
		logger.info("Find elements by id: " + id);
		waitForElementToBeLoadedById(id);
		return driver.findElements(By.id(id));
	}

	public List<WebElement> findElementsByTagName(String tagName) {
		return driver.findElements(By.tagName(tagName));
	}

	public WebElement findElementByTagName(String tagName) {
		logger.info("Find an element by tagName: " + tagName);
		waitForElementToBeLoadedByTagName(tagName);
		return driver.findElement(By.tagName(tagName));
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public WebElement findElementByName(String name) {
		logger.info("Find an element by name: " + name);
		waitForElementToBeLoadedByName(name);
		return driver.findElement(By.name(name));
	}

	public List<WebElement> findElementsByName(String name) {
		logger.info("Find an element by name: " + name);
		waitForElementToBeLoadedByName(name);
		return driver.findElements(By.name(name));
	}

	public WebElement findElementByXPath(String xpath) {
		logger.info("Find an element by xpath: " + xpath);
		waitForElementToBeLoaded(xpath);
		return driver.findElement(By.xpath(xpath));
	}

	public List<WebElement> findElementsByXPath(String xpath) {
		logger.info("Find elements by xpath: " + xpath);
		waitForElementToBeLoaded(xpath);
		return driver.findElements(By.xpath(xpath));
	}

	public WebElement findElementByClassName(String classname) {
		logger.info("Find element by class: " + classname);
		waitForElementToBeLoadedByClassName(classname);
		return driver.findElement(By.className(classname));
	}

	public List<WebElement> findElementsByClassName(String classname) {
		logger.info("Find elements by class: " + classname);
		waitForElementToBeLoadedByClassName(classname);
		return driver.findElements(By.className(classname));
	}

	public WebElement findElementByCss(String css) {
		logger.info("Find elements by css: " + css);
		waitForElementToBeLoadedByCss(css);
		return driver.findElement(By.cssSelector(css));
	}

	public List<WebElement> findElementsByCss(String css) {
		logger.info("Find elements by css: " + css);
		waitForElementToBeLoadedByCss(css);
		return driver.findElements(By.cssSelector(css));
	}

	public WebElement findElementByLinkText(String linkText) {
		logger.info("Find elements by linkText: " + linkText);
		waitForElementToBeLoadedByLinkText(linkText);
		return driver.findElement(By.linkText(linkText));
	}

	public List<WebElement> findElementsByLinkText(String linkText) {
		logger.info("Find elements by linkText: " + linkText);
		waitForElementToBeLoadedByLinkText(linkText);
		return driver.findElements(By.linkText(linkText));
	}

	// Click the element at x, y coordinates
	public void clickAtXY(WebElement e, int x, int y) {
		logger.info("clickXY..(" + x + "," + y + ")");
		Actions actions = new Actions(driver);
		actions.moveToElement(e).moveByOffset(x, y).click().perform();
	}

	// Click the element at center
	public void clickAtCenter(WebElement e) {
		logger.info("clickAtCenter..");
		Actions actions = new Actions(driver);
		actions.moveToElement(e).click().perform();
	}

	/**
	 * Take screenshot
	 * 
	 * @param filePath
	 */
	public void takeScreenshot(String filePath) {
		TakesScreenshot screenshot = (TakesScreenshot) driver;
		if (screenshot == null) {
			logger.warn("Can't take the screenshot!");
			return;
		}
		File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
		File destFile = new File(filePath);
		try {
			FileUtils.copyFile(srcFile, destFile);
			org.testng.Reporter.log(destFile.getCanonicalPath(), true);
		} catch (IOException ioe) {
			logger.warn("Exception while creating the snapshot file!" + ioe.getMessage());
			ioe.printStackTrace();
		}

	}

	/**
	 * Get current time stamp to suffix to filenames
	 * 
	 * @return
	 */
	public String getCurrentDateTimeDefault() {
		return Calendar.getInstance().getTime().toString();
	}

	/**
	 * Get current time stamp in given format
	 * 
	 * @return
	 */
	public String getCurrentDateTime(String format) {
		String dateSuffix = new SimpleDateFormat(format).format(Calendar.getInstance().getTime()).toString();
		return dateSuffix;
	}

	/**
	 * Get current time stamp to suffix to filenames
	 * 
	 * @return
	 */
	public String getCurrentDateTimeForSuffix() {
		return getCurrentDateTime("MM_dd_yyyy_hh_mm_ss");
	}

	/**
	 * Wait for some given time secs.
	 * 
	 * @param secs
	 */
	public void waitSimply(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitSimply(double secs) {
		try {
			Thread.sleep((long) (secs * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void waitForElementToBeLoaded(String xpath) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.xpath(xpath);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

	}

	public void waitForElementToBeLoadedByCss(String css) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.cssSelector(css);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeLoadedById(String id) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.id(id);

		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeLoadedByTagName(String tagname) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.tagName(tagname);

		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeLoadedByClassName(String classname) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.className(classname);

		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeLoadedByName(String name) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.name(name);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitForElementToBeLoadedByLinkText(String linkText) {
		waitSimply(3);
		// Wait for login Web Element to be loaded
		By locator = By.linkText(linkText);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement waitForElementToBeClickable(String xpath) {
		By locator = By.xpath(xpath);
		WebElement we = findElementByXPath(xpath);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		wait.until(ExpectedConditions.elementToBeClickable(we));
		return we;
	}

	public WebElement waitForElementIdToBeClickable(String id) {
		By locator = By.id(id);
		WebElement we = findElementById(id);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		wait.until(ExpectedConditions.elementToBeClickable(we));
		return we;
	}

	public WebElement waitForElementCssToBeClickable(String css) {
		By locator = By.cssSelector(css);
		WebElement we = findElementByCss(css);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		wait.until(ExpectedConditions.elementToBeClickable(we));
		return we;
	}

	public WebElement waitForElementLinkTextToBeClickable(String linkText) {
		By locator = By.linkText(linkText);
		WebElement we = findElementByCss(linkText);
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		wait.until(ExpectedConditions.elementToBeClickable(we));
		return we;
	}

	public void executeScript(String js) {
		logger.info("Executing..." + js);
		executeScript(js, "");
	}

	public void executeScript(String js, String args) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(js, args);
	}

	public void scrollPageDown() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scrollBy(0,250)", "");
	}

	public void scrollPageUp() {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scrollBy(0,-250)", "");
	}

	public void scrollPageDown(int numPixels) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scrollBy(0," + numPixels + ")", "");
	}

	public void scrollPageUp(int numPixels) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("scrollBy(" + numPixels + ",0)", "");
	}

	public String getToday(String format) {
		if (format == null) {
			format = "dd,MMM,yyyy";
		}
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		return dateformat.format(cal.getTime());

	}

	/**
	 * Accept OK
	 */
	public void acceptAlert() {
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().accept();
	}

	/**
	 * Cancel Alert
	 */
	public void cancelAlert() {
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().dismiss();

	}

	/**
	 * Accept OK
	 */
	public String getAlertMessage() {
		WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
		wait.until(ExpectedConditions.alertIsPresent());
		return driver.switchTo().alert().getText();
	}

}
