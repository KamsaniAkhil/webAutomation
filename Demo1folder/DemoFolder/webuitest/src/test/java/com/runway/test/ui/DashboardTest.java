package com.runway.test.ui;

import java.io.IOException;
import java.lang.reflect.Constructor;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
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
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gurock.testrail.APIException;
import com.runway.test.ui.common.utils.UITestSeleniumHelper;
import com.testRail.testManager.TestRailManager;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class DashboardTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("DashboardTest");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "JR365@ims22!@");
	protected String TestcaseId;
	private String countNotification = null;
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();
	TestRailManager tr = new TestRailManager();
	public static WebDriver driver;
	Calendar cal = Calendar.getInstance();

	// Getting present day
	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

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

	public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		IRetryAnalyzer retry = testannotation.getRetryAnalyzer();

		if (retry == null) {
			testannotation.setRetryAnalyzer(DashboardTest.class);
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

	/* TestCaseID is C18994 */
	@Test(enabled = true, priority = 1)
	public void landingWelcomePage()  {

		webAppHelper.loginPage(username, password);
		webAppHelper.waitForElementToBeLoadedByClassName("welcomeMessage");
		// TestcaseId = "2";
		logger.info("Getting display welcome message");
		String welcome = webAppHelper.findElementByClassName("welcomeMessage").getText();

		logger.info("Asserting welcome text");
		Assert.assertEquals(welcome, "Welcome to AIMS360");

		logger.info("getting logo position css value");
		String logoPosition = webAppHelper.findElementByXPath("//div[contains(@class,'pt-4')]//child::img")
				.getCssValue("vertical-align");

		logger.info("Asserting Logo css value");
		Assert.assertEquals(logoPosition, "middle");

//		if (logoPosition.contains("middle")) {
//
//			tr.addResultForTestCase("1", "1", tr.TEST_CASE_PASSED_STATUS, "");
//		} else {
//			tr.addResultForTestCase("1", "1", tr.TEST_CASE_PASSED_STATUS, "");
//
//		}

	}

	/* TestCaseId - C19003 */
	@Test(enabled = true, priority = 2, dependsOnMethods = "landingWelcomePage")
	public void SidebarMenuOptions() {

		logger.info("Getting Dashboard Icon");
		List<WebElement> sideBarLogo = webAppHelper
				.findElementsByXPath("//*[local-name()='svg' and (@class='c-sidebar-nav-icon')]");// .get(1)
		// .getAttribute("xmlns");
		for (int i = 0; i < sideBarLogo.size(); i++) {

			logger.info("Asserting OTS By Date Icon");
			Assert.assertTrue(sideBarLogo.get(i).getAttribute("xmlns").contains("http://www.w3.org/2000/svg"));

		}

		logger.info("Getting Dashboard name");
		String dashboardName = webAppHelper.findElementByXPath("//a[contains(@class,'c-sidebar-nav-link')]").getText();

		logger.info("Asserting Dashboard Name");
		Assert.assertTrue(dashboardName.contains("Dashboard"));

		logger.info("Getting Reports name and Icon");
		String reports = webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(0).getText();
		String wms = webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(1).getText();

		logger.info("Asserting Side Bar menu option Icon Text");
		Assert.assertEquals(reports, "Reports");
		Assert.assertEquals(wms, "WMS");

	}

	/* TestCaseId - C18995 */
	@Test(enabled = true, priority = 3, dependsOnMethods = "SidebarMenuOptions")
	public void headerTitleName() throws IOException, APIException {

		logger.info("Getting Header title Names");
		String DashboardText = webAppHelper.findElementByXPath("//li[@role='presentation']").getText();
		String clientName = webAppHelper.findElementByXPath("//a[contains(@class,'pl-1')]").getText().trim();

		logger.info("Asserting Header title Names");
		Assert.assertEquals(DashboardText, "Dashboard");
		Assert.assertEquals(clientName, "JR365");

	}

	/* TestCaseId - C19001 */
	@Test(enabled = true, priority = 4, dependsOnMethods = "landingWelcomePage")
	public void sideBarLogoTest() {

		logger.info("Getting side bar logo path");
		String sideBarLogo = webAppHelper.findElementsByXPath("//a[contains(@class , 'c-sidebar-brand ')]//child::img")
				.get(0).getAttribute("src");

		logger.info("Asserting side bar Logo");
		Assert.assertTrue(sideBarLogo.contains("logo-light"));

		logger.info("Getting Side bar logo css value");
		String logoCssValue = webAppHelper.findElementByClassName("c-sidebar-brand-full").getCssValue("vertical-align");

		logger.info("Asserting Logo css value");
		Assert.assertEquals(logoCssValue, "middle");
	}

	/* TestCaseId - C19000 */
	@Test(enabled = true, priority = 5)
	public void headerToggleIconTest() {

		logger.info("Getting header toggle icon class name");
		String headerToggleIconCSS = webAppHelper
				.findElementByXPath("//*[contains(@class , 'c-sidebar-brand')]//parent::div").getAttribute("class");

		logger.info("Asserting header toggle icon class name");
		Assert.assertTrue(headerToggleIconCSS.contains(" c-sidebar-show"));

		logger.info("Clicking Header toggle icon");
		webAppHelper.findElementByXPath("//button[contains(@class , 'd-md-down-none')]//child::span").click();

		logger.info("Getting header toggle icon class name");
		String changedHeaderToggleIconCSS = webAppHelper
				.findElementByXPath("//*[contains(@class , 'c-sidebar-brand')]//parent::div").getAttribute("class");

		logger.info("Asserting changed header toggle icon class name");
		Assert.assertTrue(changedHeaderToggleIconCSS.contains("c-sidebar c-sidebar-dark c-sidebar-fixed"));

		logger.info("Clicking Header toggle icon");
		webAppHelper.findElementByXPath("//button[contains(@class , 'd-md-down-none')]//child::span").click();

	}

	/* TestCaseId - C18997 */
	@Test(enabled = true, priority = 6)
	public void themeIconTest() {

		logger.info("Getting theme icon class name");
		String themeiconCSS = webAppHelper.findElementByXPath("//*[contains(@class , 'c-app')]").getAttribute("class");

		logger.info("Asserting theme icon class name");
		Assert.assertTrue(themeiconCSS.contains("c-default-layout"));

		logger.info("clicking theme icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'profile-custiom-wording')]").click();
		webAppHelper.findElementByXPath("//*[contains(@class,'c-d-legacy-none')]").click();

		logger.info("Getting theme icon class name");
		String changedthemeiconCSS = webAppHelper.findElementByXPath("//*[contains(@class , 'c-app')]")
				.getAttribute("class");

		logger.info("Asserting theme icon changed class name");
		Assert.assertTrue(changedthemeiconCSS.contains("c-dark-theme"));

		logger.info("clicking theme icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'profile-custiom-wording')]").click();
		webAppHelper.findElementByXPath("//*[contains(@class,'c-d-legacy-none')]").click();

	}

	/* TestCaseId - C18998 */
	@Test(enabled = true, priority = 7)
	public void bellIconTest() {

		webAppHelper.waitSimply(3);
		logger.info("Getting bell icon class name");
		String bellIconCSS = webAppHelper
				.findElementByXPath("//*[text()=' notifications']//parent::div//parent::div//parent::li")
				.getAttribute("class");

		logger.info("Asserting bell icon class name");
		Assert.assertTrue(bellIconCSS.contains("c-header-nav-item mx-2 dropdown nav-item"));

		logger.info("clicking notification icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-item ')]").click();

		logger.info("Getting notification messages in the dashboard");
		webAppHelper.waitForElementToBeLoaded("//div[contains(@class ,'dropdown-header')]//child::strong");
		String notificationMsge = webAppHelper
				.findElementByXPath("//div[contains(@class ,'dropdown-header')]//child::strong").getText();

		logger.info("Asserting notification message in the Dashboard");
		Assert.assertEquals(notificationMsge, "You have 0 notifications");

		logger.info("Getting bell icon class name");
		String bellIconCSSChanged = webAppHelper
				.findElementByXPath("//*[text()=' notifications']//parent::div//parent::div//parent::li")
				.getAttribute("class");

		logger.info("Asserting bell icon changed class name");
		Assert.assertTrue(bellIconCSSChanged.contains(" show"));

		logger.info("clicking notification icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-item ')]").click();

	}

	@Test(enabled = true, priority = 8)
	public void profileName() {

		logger.info("getting profile icon first two char");
		String uName = username.substring(0, 2);
	
		logger.info("Getting profile name");
		String profileName = webAppHelper.findElementByXPath("//div[contains(@class , 'profile-custiom-wording')]")
				.getText();

		logger.info("Asserting Profile icon");
		//Assert.assertEquals(uName, profileName);
		Assert.assertFalse(profileName.contains(" "));
		Assert.assertFalse(profileName.contains("uu"));
		

	}

	/* TestCaseId - C18999 */
	@Test(enabled = true, priority = 9)
	public void profileIconTestBeforeClick() {

		logger.info("Getting profile icon class name");
		webAppHelper.waitForElementToBeLoaded("//div[contains(@class , 'c-avatar')]//parent::a//parent::li");
		String profileIconCSS = webAppHelper
				.findElementByXPath("//div[contains(@class , 'c-avatar')]//parent::a//parent::li")
				.getAttribute("class");

		logger.info("Asserting profile icon class name");
		Assert.assertTrue(profileIconCSS.contains("c-header-nav-items "));

		logger.info("clicking profile icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-items')]").click();

		logger.info("Getting profile icon changed class name");
		String profileIconCSS1 = webAppHelper
				.findElementByXPath("//div[contains(@class , 'c-avatar')]//parent::a//parent::li")
				.getAttribute("class");

		logger.info("Asserting profile icon changed class name");
		Assert.assertTrue(profileIconCSS1.contains(" show"));

		logger.info("clicking profile icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-items')]").click();

	}

	/* TestCaseId - C19005 */
	@Test(enabled = true, priority = 10)
	public void NavigateToOTSByDate() throws IOException, APIException {

		logger.info("clicking reports");

		webAppHelper.findElementByXPath("//*[text()='Reports']").click();

		logger.info("clicking Ots By Date ");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

		logger.info("getting side nav title name Dashboard");
		String dashboard = webAppHelper.findElementByXPath("//*[contains(@class,'badge-info')]//parent::a").getText();

		logger.info("Asserting of OTS Date card");
		Assert.assertTrue(dashboard.contains("Dashboard"));

		logger.info("clicking  side nav title name Dashboard");
		webAppHelper.findElementByXPath("//*[contains(@class,'badge-info')]//parent::a").click();

	}

	/* TestCaseId - C19022 */
	@Test(enabled = true, priority = 11)
	public void footerElements() {

		logger.info("Getting Icon and Version number");
		String versionText = webAppHelper.findElementByXPath("//span[@role='button']").getText();
		String svgIcon = webAppHelper.findElementByXPath("//span[@role='button']/*[local-name()='svg']")
				.getAttribute("xmlns");

		logger.info("Asserting version Text and Svg Icon image");
		Assert.assertEquals(versionText, "V2022.11.10");
		Assert.assertTrue(svgIcon.contains("http://www.w3.org/2000/svg"));

		logger.info("Getting Default footer work");
		String defaultFooter = webAppHelper.findElementByXPath("//span[@role='button']/*[local-name()='svg']")
				.getAttribute("class");

		logger.info("Asserting Default footer in page");
		Assert.assertTrue(defaultFooter.contains("pin"));

		logger.info("Getting company name and Icon");
		String companyName = webAppHelper.findElementsByXPath("//div[@class ='text-left']//span").get(1).getText();

		logger.info("Asserting Company name and Icon");
		Assert.assertTrue(companyName.contains(" JOY RIDE 365 LLC dba THINK ROYLN"));

		logger.info("Getting Database name and Icon class name");
		String databaseIcon = webAppHelper.findElementsByXPath("//div[@class ='text-left']//i").get(0)
				.getAttribute("class");
		String databaseName = webAppHelper.findElementByXPath("//i[contains(@class,'fa-database')]//parent::span").getText();
		logger.info("Asserting Database name and Icon");
		Assert.assertEquals(databaseIcon, "fas fa-database");
		Assert.assertTrue(databaseName.contains("JR365Data1"));

		logger.info("Getting copyright Name in footer");
		String copyRightName = webAppHelper.findElementByXPath("//span[@class='ml-1']").getText();

		logger.info("Asserting copyright Name");
		Assert.assertEquals(copyRightName, "© 2023 AIMS360");

	}
	
	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}

}
