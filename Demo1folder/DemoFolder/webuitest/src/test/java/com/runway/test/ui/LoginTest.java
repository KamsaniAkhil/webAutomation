package com.runway.test.ui;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import com.runway.test.ui.common.utils.UITestSeleniumHelper;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class LoginTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("LoginTest");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "Creai@ims22!@");
	public static String URLSCANTRUE = "https://dev-creai.aims360runway.com/login?scan=true";
	public static String URLSCANFALSE = "https://dev-creai.aims360runway.com/login?scan=false";
	public static String URL = "https://dev-creai.aims360runway.com/";

	private String countNotification = null;
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();

	public static WebDriver driver;
	Calendar cal = Calendar.getInstance();

	// Getting present day
	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

	@BeforeClass
	void setup() {
		webAppHelper.openBrowser("chrome");
		webAppHelper.maximizeWindow();

		logger.info("Navigating to the URL");
		webAppHelper.navigateTo("https://dev-creai.aims360runway.com/");
		webAppHelper.waitSimply(3);
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		currentRunningMethodName = method.getName();

	}

	public boolean retry(ITestResult result) {
		if (retryCount < maxRetryCount) {
			System.out.println("Retrying test " + result.getName() + " with status "
					+ getResultStatusName(result.getStatus()) + " for the " + (retryCount + 1) + " time(s).");
			retryCount++;

			return true;
		}
		return false;
	}

	public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		IRetryAnalyzer retry = testannotation.getRetryAnalyzer();

		if (retry == null) {
			testannotation.setRetryAnalyzer(LoginTest.class);
		}

	}

	public String getResultStatusName(int status) {
		String resultName = null;
		if (status == 1)
			resultName = "SUCCESS";
		else if (status == 2)
			resultName = "FAILURE";
		else if (status == 3)
			resultName = "SKIP";
		return resultName;
	}

	/* TestCaseId - C17893 */
	@Test(enabled = true , priority = 1) // ,groups = {"SmokeTest"})
	public void loginComponentsWithScanIsTrue() {

//		webAppHelper.setup();
		webAppHelper.navigateTo(URLSCANTRUE);
		logger.info("Getting Login title name");
		String loginTitle = webAppHelper.findElementByXPath("//h5[contains(@class,'m-0')]").getText();

		logger.info("Asserting login title name");
		Assert.assertEquals(loginTitle, "Runway Login");

		logger.info("Getting Scan Id Card component");
		String scanIdName = webAppHelper.findElementByXPath("//label[@for='scanner']").getText();

		logger.info("Getting OR name in the Login screen");
		String orName = webAppHelper.findElementByXPath("//p[contains(@class,'m-0')]").getText();

		logger.info("Asserting orName");
		Assert.assertEquals(orName, "OR");

		logger.info("Getting Email name");
		String email = webAppHelper.findElementByXPath("//label[@for='userName']").getText();

		logger.info("Asserting email name");
		Assert.assertEquals(email, "Email");

		logger.info("Getting password name");
		String passwordName = webAppHelper.findElementByXPath("//label[@for='password']").getText();

		logger.info("Asserting Password name");
		Assert.assertEquals(passwordName, "Password");

	}

	/* TestCaseId - C17895 */
	@Test(enabled = true, priority = 2)
	public void loginComponentsWithScanIdIsFalse() {

		webAppHelper.navigateTo(URLSCANFALSE);

		logger.info("Getting label name in login screen");
		webAppHelper.waitForElementToBeLoadedByClassName("px-5");
		String labelName = webAppHelper.findElementByClassName("px-5").getText();

		logger.info("Getting label name's");
		Assert.assertFalse(labelName.contains("Scan ID Card"));

		logger.info("Getting Email name");
		String email = webAppHelper.findElementByXPath("//label[@for='userName']").getText();

		logger.info("Asserting email name");
		Assert.assertEquals(email, "Email");

		logger.info("Getting password name");
		String passwordName = webAppHelper.findElementByXPath("//label[@for='password']").getText();

		logger.info("Asserting Password name");
		Assert.assertEquals(passwordName, "Password");

	}

	/* TestCaseId - C17897 */
	@Test(enabled = true, priority = 3)
	public void loginWithEmptyDetails() {
		logger.info("Getting Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Username Validation message");
		String userNameMsge = webAppHelper.findElementsByClassName("text-danger").get(0).getText();

		logger.info("Asserting UserName validation message");
		Assert.assertEquals(userNameMsge, "Please enter email");

		logger.info("Getting Password Validation message");
		String passwordMsge = webAppHelper.findElementsByClassName("text-danger").get(1).getText();

		logger.info("Getting Password validation message");
		Assert.assertEquals(passwordMsge, "Please enter password");

	}

	/* TestCaseId - C17968 */
	@Test(enabled = true, priority = 4)
	public void loginEmptyUserNameAndValidPassword() {

		webAppHelper.navigateTo(URLSCANTRUE);

		webAppHelper.waitForElementToBeLoadedByName("password");
		logger.info("sending password");
		webAppHelper.findElementByName("password").sendKeys(password);

		logger.info("Getting Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Password Validation message");
		String validationMsge = webAppHelper.findElementsByClassName("text-danger").get(0).getText();

		logger.info("Getting validation error message ");
		Assert.assertEquals(validationMsge, "Please enter email");

	}

	/* TestCaseId - C17967 */
	@Test(enabled = false, priority = 5)
	public void loginWithValidUserNameAndEmptyPassword() {

		webAppHelper.refreshBrowser();
		webAppHelper.waitForElementToBeLoadedByName("password");
		logger.info("sending valid username");
		webAppHelper.findElementByName("userName").sendKeys(username);

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Password Validation message");
		String validationMsge = webAppHelper.findElementByClassName("text-danger").getText();

		logger.info("Asserting validation message");
		Assert.assertEquals(validationMsge, "Please enter password");

	}

	/* TestCaseId - C17964 */
	@Test(enabled = true, priority = 6)
	public void loginWithValidUserNameAndInvalidPassword() {

		webAppHelper.refreshBrowser();

		webAppHelper.waitForElementToBeLoadedByName("userName");
		logger.info("sending Valid Username");
		webAppHelper.findElementByName("userName").sendKeys(username);

		logger.info("sending InValid password");
		webAppHelper.findElementByName("password").sendKeys("creai@ims221@");

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Scan Id error message");
		String scanIDValidation1 = webAppHelper.findElementByXPath("//span[contains(@class,'text-center')]").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation1, "*Invalid details!");

	}

	/* TestCaseId - C17965 */
	@Test(enabled = true, priority = 7)
	public void inValidUserNameAndValidPassword() {

		webAppHelper.refreshBrowser();

		webAppHelper.waitForElementToBeLoadedByName("userName");
		logger.info("sending Invalid username");
		webAppHelper.findElementByName("userName").sendKeys("varungopi@aims360.com");

		logger.info("sending password");
		webAppHelper.findElementByName("password").sendKeys(password);

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();
       
		logger.info("Getting Scan Id error message");
		String scanIDValidation1 = webAppHelper.findElementByXPath("//span[contains(@class,'text-center')]").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation1, "*Invalid details!");

	}

	/* TestCaseId - C17966 */
	@Test(enabled = true, priority = 8)
	public void invalidUserNameAndPassword() {

		webAppHelper.refreshBrowser();
		webAppHelper.waitForElementToBeLoadedByName("userName");
		logger.info("sending Invalid UserName");
		webAppHelper.findElementByName("userName").sendKeys("v-arungopi@aims.com");

		logger.info("sending password");
		webAppHelper.findElementByName("password").sendKeys("Jr365@ims22!");

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();
      
		logger.info("Getting Scan Id error message");
		String scanIDValidation1 = webAppHelper.findElementByXPath("//span[contains(@class,'text-center')]").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation1, "*Invalid details!");

	}

	/* TestCaseId - C17967 */
	@Test(enabled = true, priority = 9)
	public void validUserNameAndEmptyPassword() {

		webAppHelper.refreshBrowser();
		webAppHelper.waitForElementToBeLoadedByName("userName");
		logger.info("sending Invalid UserName");
		webAppHelper.findElementByName("userName").sendKeys(username);

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Validation Error Message's");
		String emailValidation = webAppHelper.findElementsByClassName("text-danger").get(0).getText();

		logger.info("Asserting Validation message's");
		Assert.assertEquals(emailValidation, "Please enter password");

	}

	/* TestCaseId - C17968 */
	@Test(enabled = true, priority = 10)
	public void emptyUserNameAndvalidPassword() {

		webAppHelper.refreshBrowser();
		webAppHelper.waitForElementToBeLoadedByName("userName");
		logger.info("sending password");
		webAppHelper.findElementByName("password").sendKeys(password);

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Validation Error Message's");
		String emailValidation = webAppHelper.findElementsByClassName("text-danger").get(0).getText();

		logger.info("Asserting Validation message's");
		Assert.assertEquals(emailValidation, "Please enter email");
	}

	/* TestCaseId - C17963 */
	@Test(enabled = true, priority = 11)
	public void invalidUserNameInScanId() {

		webAppHelper.refreshBrowser();
		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys("v-arungopi;Creai@ims22!@");

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Scan Id error message");
		String scanIDValidation = webAppHelper.findElementByXPath("//span[contains(@class,'text-center')]").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation, "*Invalid details!");

		// Invalid Password In Scan Id Details
		webAppHelper.refreshBrowser();
		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys("v-arungopi@aims360.com;creai@ims22!@");

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Scan Id error message");
		String scanIDValidation1 = webAppHelper.findElementByXPath("//span[contains(@class,'text-center')]").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation1, "*Invalid details!");

		/* Without Semicolon Scan ID Validations */
		webAppHelper.refreshBrowser();
		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys("v-arungopi@aims360.comCreai@ims22!@");

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting Scan Id error message");
		String scanIDValidation2 = webAppHelper.findElementByClassName("text-danger").getText();

		logger.info("Asserting Scan Id validation Message");
		Assert.assertEquals(scanIDValidation2, "*Invalid QR/Barcode!");

	}

	/* TestCaseId - C18022 */
	@Test(enabled = true, priority = 12)
	public void inValidScanIDandValidUserNameAndPassword() {

		webAppHelper.refreshBrowser();

		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys(username + " " + password);

		webAppHelper.loginPage(username, password);

		logger.info("Selecting profile icon");
		webAppHelper.waitForElementToBeClickable("//div[contains(@class,'profile-custiom-wording')]").click();
		
		logger.info("clicking Logout option");
		webAppHelper.findElementByXPath("//a[text()='Logout']").click();

	}
	
	/* TestCaseId - C18023 */
	@Test(enabled = false, priority = 13)
	public void validScanIdAndInvalidUserNamePassword() {
		
		webAppHelper.refreshBrowser();
        webAppHelper.waitForElementToBeLoadedByName("userName");    
		webAppHelper.loginPage("V-Arungopi@aims360.com", "Jr365@@ims22!@");
        
		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys(username + ";" + password);
      
		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();
       
		logger.info("Selecting profile icon");
		//webAppHelper.waitForElementToBeClickable("//div[contains(@class,'profile-custiom-wording')]");
		webAppHelper.findElementByXPath("//div[contains(@class,'profile-custiom-wording')]").click();

		logger.info("clicking Logout option");
		webAppHelper.findElementByXPath("//a[text()='Logout']").click();

		
	}
     
	
	/* TestCaseId - C17893 */
	@Test(enabled = false, priority = 14, groups = { "SmokeTest" })
	public void validScanID() {

		webAppHelper.refreshBrowser();

		logger.info("Getting Scan Id component");
		webAppHelper.findElementByName("scanner").sendKeys(username + ";" + password);

		logger.info("Clicking Login button");
		webAppHelper.findElementByXPath("//button[text()='Login']").click();

		logger.info("Getting welcome message after login");
		webAppHelper.waitForElementToBeLoadedByClassName("welcomeMessage");
		String welcomeMsge = webAppHelper.findElementByClassName("welcomeMessage").getText();

		logger.info("Asserting welcome message");
		Assert.assertEquals(welcomeMsge, "Welcome to AIMS360");

	}

	@AfterClass
	public void unsetup() {
		webAppHelper.quitBrowser();
	}
}
