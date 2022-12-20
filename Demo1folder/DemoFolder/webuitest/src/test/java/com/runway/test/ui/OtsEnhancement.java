package com.runway.test.ui;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass; 
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.runway.test.ui.common.utils.PropsHandler;
import com.runway.test.ui.common.utils.UITestSeleniumHelper;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class OtsEnhancement implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("LoginTest");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "JR365@ims22!@");

	private String countNotification = null;
	private String currentUrl = null;
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();
	PropsHandler file = new PropsHandler();
	public static WebDriver driver;
	
	@BeforeClass
	public void setup() {
		webAppHelper.openBrowser("chrome");
		webAppHelper.maximizeWindow();

		logger.info("Navigating to the URL");
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com");
		webAppHelper.waitSimply(3);
	}


	@BeforeMethod
	public void beforeMethod(Method method) {
		currentRunningMethodName = method.getName();
	}

	@Override
	public boolean retry(ITestResult result) {
		if (retryCount < maxRetryCount) {
			System.out.println("Retrying test " + result.getName() + " with status "
					+ getResultStatusName(result.getStatus()) + " for the " + (retryCount + 1) + " time(s).");
			retryCount++;

			return true;
		}
		return false;
	}

	@Override
	public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		IRetryAnalyzer retry = testannotation.getRetryAnalyzer();

		if (retry == null) {
			testannotation.setRetryAnalyzer(OtsEnhancement.class);
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
    
	
	
	/* TestCaseId - C18123 */
	@Test(enabled = true , priority = 1)
	public void sideBarMainMenuForFullyQualifiedURL() {
		
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/reports");
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
		webAppHelper.loginPage(username, password);
		logger.info("Getting sideBar menu options");
		String sideBarMenuOptions = webAppHelper.findElementsByXPath("//ul[contains(@class,'h-100')]//child::li").get(1).getText();
		
		logger.info("Getting Default side bar drop down options is opened class name");
		String defaultSideBarDropDown = webAppHelper.findElementsByXPath("//ul[contains(@class,'h-100')]//li").get(0).getAttribute("class");
		
		logger.info("Getting Default class name for side bar menu");
		Assert.assertTrue(defaultSideBarDropDown.contains("c-show"));
		
		logger.info("Asserting sideBar option");
		Assert.assertTrue(sideBarMenuOptions.contains("OTS By Date"));
		
	}
	
	/* TestCaseId - C18121 */
	@Test(enabled = true, priority  = 2)
	public void sideBarSubMenuForFullyQualifiedURL() {
		
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/reports/otsbydate");
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
		webAppHelper.loginPage(username, password);
		
		logger.info("Getting sideBar menu options");
		String sideBarMenuOptions = webAppHelper.findElementsByXPath("//ul[contains(@class,'h-100')]//li").get(1).getText();
		
		logger.info("Getting OTS By Date header title name");
		String headerTitle = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1).getText();
		
		logger.info("Asserting OTS By Date title name");
		Assert.assertEquals(headerTitle, "OTS By Date");
		
		logger.info("Asserting side Bar menu options");
		Assert.assertTrue(sideBarMenuOptions.contains("OTS By Date"));
	
	}
	
	/* TestCaseId - C18126 */
	@Test(enabled = false, priority = 3)
    public void refreshesTheApplicationForFullyQualifiedURL() {
		
		webAppHelper.refreshBrowser();
		
		logger.info("Getting sideBar menu options");
		webAppHelper.waitForElementToBeLoaded("//ul[contains(@class,'h-100')]");
		String sideBarMenuOptions = webAppHelper.findElementByXPath("//ul[contains(@class,'h-100')]").getText();
		
		logger.info("Getting OTS By Date header title name");
		String headerTitle = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1).getText();
		
		logger.info("Asserting OTS By Date title name");
		Assert.assertEquals(headerTitle, "OTS By Date");
		
		logger.info("Asserting side Bar menu options");
		Assert.assertTrue(sideBarMenuOptions.contains("Reports"));
		
		
	}
	@Test(enabled = true, priority = 4)
	public void reportsUrlPatternTest() {

		logger.info("Getting current url");
		currentUrl = webAppHelper.getCurrentUrl();
		currentUrl = currentUrl.split("#")[0];
		currentUrl = currentUrl + "#/Reports";
		webAppHelper.navigateTo(currentUrl);

		logger.info("Getting Helper message in the body");
		String helpText = webAppHelper.findElementByXPath("//label[@class='welcomeMessage']//child::h4").getText();

		logger.info("Asserting Helper message");
		Assert.assertEquals(helpText, "Please select an item from left menu to get started.");

		logger.info("Getting side bar menu details");
		String menuName = webAppHelper.findElementByClassName("c-sidebar-nav-dropdown-toggle").getText();

		logger.info("Asserting menu Name");                        
		Assert.assertEquals(menuName, "Reports");
		
		logger.info("Getting Default dropdown  class name");
		String dropDownClsName = webAppHelper.findElementsByXPath("//ul[contains(@class,'c-sidebar-nav')]//child::li")
				.get(0).getAttribute("class");

		logger.info("Asserting dropdown class name");
		Assert.assertTrue(dropDownClsName.contains("c-sidebar-nav-dropdown"));
	}
	
	/* TestCaseId - C18120 */
	@Test(enabled = false, priority = 5)
	public void otsByDateUrlTest() {

		logger.info("Getting current url");
		currentUrl = webAppHelper.getCurrentUrl();
		currentUrl = currentUrl.split("#")[0];
		currentUrl = currentUrl + "#/Reports/otsbydate";
		webAppHelper.navigateTo(currentUrl);
		logger.info("Getting side bar menu options");
		String sideMenuName = webAppHelper.findElementByXPath("//div[contains(@class,'c-sidebar-dark')]").getText();

		logger.info("Asserting side bar wms menu");
		Assert.assertTrue(sideMenuName.contains("WMS"));
		Assert.assertTrue(sideMenuName.contains("OTS By Date"));

		logger.info("Getting header title name");
		String titleName = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting OTS By Date page title name");
		Assert.assertEquals(titleName, "OTS By Date");

	}
	
	/* TestCaseId - C18128 */
	@Test(enabled = true, priority = 6)
	public void logoutForFullyQualifiedURL() {
		
		logger.info("Log out from the page");
		webAppHelper.findElementByClassName("c-avatar").click();

		logger.info("clicking Logout option");
		webAppHelper.findElementByXPath("//a[text()='Logout']").click();
		
		logger.info("Getting current URL");
		webAppHelper.waitSimply(2);
		String url = webAppHelper.getCurrentUrl();
		
		logger.info("Asserting URL");
		Assert.assertEquals(url, "https://dev-jr365.aims360runway.com/reports/otsbydate");
	
	}
	
	/* TestCaseId - C18129 */
	@Test(enabled = true, priority = 7)
	public void wrongURLendPointFOrMainMenuEndPoint() {

		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/report");
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
		webAppHelper.loginPage(username, password);
		
		logger.info("Getting Header title Name");
		String headerTitleName = webAppHelper.findElementByXPath("//li[contains(@class,'breadcrumb-item')]").getText();
		
		logger.info("Asserting Dashboard title name");
		Assert.assertEquals(headerTitleName, "Dashboard");
		
		webAppHelper.refreshBrowser();
		
		logger.info("Getting side bar menu options");
		String sideBarMenuOption = webAppHelper.findElementByXPath("//ul[contains(@class,'h-100')]").getText();
		
		logger.info("Asserting side bar menu options");
		Assert.assertTrue(sideBarMenuOption.contains("Dashboard"));
		Assert.assertTrue(sideBarMenuOption.contains("Reports"));
		Assert.assertTrue(sideBarMenuOption.contains("WMS"));
		
	}
	
	/* TestCaseId - C18130 */
	@Test(enabled = true ,priority = 8)
	public void wrongURLendPointForSubMenuEndPoint() {
		
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/report/ots");
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
	    webAppHelper.loginPage(username, password);
		
		logger.info("Getting Header title Name");
		String headerTitleName = webAppHelper.findElementByXPath("//li[contains(@class,'breadcrumb-item')]").getText();
		
		logger.info("Asserting Dashboard title name");
		Assert.assertEquals(headerTitleName, "Dashboard");
		
		webAppHelper.refreshBrowser();
		
		logger.info("Getting side bar menu options");
		String sideBarMenuOption = webAppHelper.findElementByXPath("//ul[contains(@class,'h-100')]").getText();
		logger.info("Asserting side bar menu options");
		Assert.assertTrue(sideBarMenuOption.contains("Reports"));
	
	}
    
	/* TestCaseId - C18130 */
	@Test(enabled = false, priority = 9)
	public void wmsSubComponentcaseSensitiveTest() {
		
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/WMS/Inquiry");
		
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
		webAppHelper.loginPage(username, password);
	
		logger.info("Getting side bar menu options");
		String sideBarMenu = webAppHelper.findElementByXPath("//ul[contains(@class,'h-100')]")
				.getText();

		logger.info("Asserting side bar menu options");
		Assert.assertTrue(sideBarMenu.contains("WMS"));
		
		logger.info("Gettng header title name");
		String headerTitleName = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting Header title to Checking topbar is enabled or disabled");
		Assert.assertEquals(headerTitleName, "Inquiry");
	}
	
	/* TestCaseId - C18129 */
	@Test(enabled = false, priority = 10)
	public void caseInsensitiveEndPoints() {
		
	webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/REPORTS");
		
		webAppHelper.waitForElementToBeLoadedByName("userName");
		
		webAppHelper.loginPage(username, password);
	
		logger.info("Getting Helper message in the body");
		String wmsHelpText = webAppHelper.findElementByXPath("//label[@class='welcomeMessage']//child::h4").getText();

		logger.info("Asserting Helper message");
		Assert.assertEquals(wmsHelpText, "Please select an item from left menu to get started.");
          
//		logger.info("Clicking log out");
//		webAppHelper.findElementByXPath("//*[text()='v-']").click();
//		webAppHelper.findElementByXPath("//*[text()='Logout']").click();
//		
	}
	
	
	/* TestCaseId - C18121 */
	@Test(enabled = true, priority = 11)
	public void applicationForFullyQualifiedURl(){
			
		  
		// webAppHelper.setup();
	 //    webAppHelper.waitSimply(4);
 	      webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/reports/otsbydate");
	     webAppHelper.loginPage(username, password);
		webAppHelper.waitForElementToBeLoaded("//*[text()='Reports']");

		logger.info("clicking to open reports");
		webAppHelper.findElementByXPath("//*[text()='Reports']").click();

		logger.info("Clicking OTS By Date");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

		logger.info("Getting top bar title name");
		String titleName = webAppHelper.findElementsByXPath("//li[@role='presentation']").get(1).getText();

		logger.info("Asserting top bar title name");
		Assert.assertEquals(titleName, "OTS By Date");

//		String topbarClass = webAppHelper
//				.findElementByXPath("//button[contains(@class,'c-header-toggler')]//parent::header")
//				.getAttribute("class");

		logger.info("Clicking theme icon");
		webAppHelper.findElementByXPath("//div[contains(@class,'profile-custiom-wording')]").click();
		webAppHelper.findElementByXPath("//button[contains(@title,'Light/Dark Mode')]").click();

		//Assert.assertTrue(topbarClass.contains("fade-in-top"));
		webAppHelper.scrollPageDown();

		logger.info("Getting OTS Calculation checkbox");
		int checkboxSize = webAppHelper.findElementsByXPath("//div[contains(@class,'py-2')]//child::label").size();

		for (int i = 0; i < checkboxSize / 2; i++) {

			webAppHelper.findElementsByXPath("//div[contains(@class,'py-2')]//child::label").get(i).click();

		}
	
		logger.info("clicking Yes option");
		webAppHelper.findElementByXPath("//option[text()='Yes']").click();

		logger.info("Clicking Date field text box");
		webAppHelper.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input").click();

		logger.info("Clicking arrow icon next month in Date picker");
		webAppHelper.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--next')]").click();

		logger.info("Selecting Day of Month class name");
		webAppHelper.findElementByXPath("//*[text() = '10' ]").click();
		webAppHelper.waitSimply(3);

		logger.info("Adding date field");
		webAppHelper.findElementByXPath("//*[local-name()='svg' and (@data-icon='plus')]").click();
    
		logger.info("Clicking Display Stock by Specified Warehouse");
		webAppHelper.findElementByXPath("//label[contains(@for,'Display')]").click();

		logger.info("Clicking tag all button");
		webAppHelper.findElementByXPath("//*[text()='Tag All']").click();
	
		logger.info("Clicking Untag All button");
		webAppHelper.findElementByXPath("//*[text()='Untag All']").click();

		webAppHelper.scrollPageUp();
		webAppHelper.findElementByXPath("//*[contains(@class , 'profile-custiom-wording')]").click();
		logger.info("clicking Logout option");
		webAppHelper.findElementByXPath("//a[text()='Logout']").click();


	}
	
	/* TestCaseId - C18127 */
	@Test(enabled = false, priority = 12)
	public void baseURL() {
		
		webAppHelper.navigateTo("https://dev-jr365.aims360runway.com/");
		logger.info("Getting side menu options");
		String sideBarMenuOptions = webAppHelper.findElementByXPath("//ul[contains(@class,'h-100')]").getText();
		
		logger.info("Asserting side bar menu options");
		webAppHelper.refreshBrowser();
		Assert.assertTrue(sideBarMenuOptions.contains("Dashboard"));
		Assert.assertTrue(sideBarMenuOptions.contains("Reports"));
		Assert.assertTrue(sideBarMenuOptions.contains("WMS"));
		
	}
	
	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}

}
