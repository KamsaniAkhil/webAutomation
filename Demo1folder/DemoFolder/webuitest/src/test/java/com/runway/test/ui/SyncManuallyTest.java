package com.runway.test.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.runway.test.ui.common.utils.UITestSeleniumHelper;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class SyncManuallyTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("SyncJobs");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "anthony@aims360.com");
	public static String password = System.getProperty("password", "Witre12!");

	WebDriver driver;
	private String countNotification = null;
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();

	@BeforeClass
	public void setup() {

		webAppHelper.openBrowser("chrome");
		webAppHelper.maximizeWindow();

		logger.info("Navigating URL");
		webAppHelper.navigateTo("https://witre.aims360runway.com/");
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

	/* TestCaseId-C19338 */
	@Test(enabled = true, priority = 1)
	public void syncManuallyUIscreen() {

		webAppHelper.waitForElementToBeLoadedByName("userName");
		webAppHelper.loginPage(username, password);

		logger.info("Clicking Sync Jobs");
		WebElement loadingTime = webAppHelper.findElementByClassName("c-sidebar-nav-dropdown-toggle");
		webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(2).click();
		webAppHelper.findElementByXPath("//a[text()='Sync Manually']").click();

		logger.info("Getting header title names");
		String headerTitleName = webAppHelper.findElementByXPath("//li[@class='breadcrumb-item']").getText();
		String syncDataheaderTitle = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting Header title name's");
		Assert.assertEquals(headerTitleName, "Monday.com");
		Assert.assertEquals(syncDataheaderTitle, "SyncManually");

		logger.info("Getting client account name");
		String clientName = webAppHelper.findElementByXPath("//a[contains(@class,'c-subheader-nav-link')]").getText();

		logger.info("getting account param in current URL ");
		String acountparam = webAppHelper.getCurrentUrl();
		String acountName = acountparam.split("=")[1];

		logger.info("Asserting account name");
		Assert.assertEquals(acountName, clientName.trim());

		logger.info("Getting card title name and asserting title name");
		String cardTitle = webAppHelper.findElementByClassName("card-header").getText();
		Assert.assertEquals(cardTitle, "Sync Manually");

	}

	/* TestCaseId-C19338 */
	@Test(enabled = true, priority = 2)
	public void syncManualyUITest1() {

		logger.info("getting sync manually run message");
		String syncText = webAppHelper.findElementByXPath("//div[@class='py-5 ']//label").getText();

		logger.info("Asserting sync Manually messages");
		Assert.assertEquals(syncText, "Do you want to run Sync operation manually ?");

		logger.info("Getting Yes&No button name");
		String yesName = webAppHelper.findElementById("check").getText().trim();
		String noName = webAppHelper.findElementById("uncheck").getText();

		logger.info("Asserting Yes and No button");
		Assert.assertEquals(yesName, "Yes");
		Assert.assertEquals(noName, "Cancel");

	}
   
	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}
}
