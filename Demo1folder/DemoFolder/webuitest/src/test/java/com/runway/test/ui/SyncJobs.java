package com.runway.test.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class SyncJobs implements IRetryAnalyzer, IAnnotationTransformer {

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

	/* TestCaseId - C19660 */
	@Test(enabled = true, priority = 1)
	public void SyncJObsTest() {

		webAppHelper.loginPage(username, password);

		logger.info("Clicking Sync Jobs");
		webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(2).click();
		webAppHelper.findElementByXPath("//a[text()='Sync Jobs']").click();

		logger.info("Selecting Show History dropdown");
		webAppHelper.findElementByXPath("//div[@class='col-md-3']//child::select").click();
		webAppHelper.findElementsByXPath("//div[@class='col-md-3']//child::select//child::option").get(1).click();

		logger.info("Clicking select option");
		webAppHelper.findElementByXPath("//option[@val='10']").click();
		// int tableRowCount = webAppHelper.findElementsByTagName("tr").size();
		webAppHelper.scrollPageDown();

		List<WebElement> rows = webAppHelper.findElementByTagName("table").findElements(By.tagName("tr"));

		Assert.assertTrue(rows.size() != 0);

		WebElement searchBox = webAppHelper.findElementByXPath("//input[@placeholder='search']");

		searchBox.click();
		String manual = "Manual";

		searchBox.sendKeys("Manual");
		List<WebElement> filteredRows = webAppHelper.findElementByTagName("table").findElements(By.tagName("tr"));

		if (filteredRows.size() != 0) {

			logger.info("Getting rows from Sync data table");
			String triggerTypetext = filteredRows.get(1).findElements(By.tagName("td")).get(2).getText();

			logger.info("Asserting Trigger type text");
			Assert.assertEquals(triggerTypetext, manual);

		}

		else {

			logger.info("Getting No filtering results text");
			String noResultText = webAppHelper.findElementByXPath("//div[contains(@class,'my-5')]").getText();

			logger.info("Asserting no filtering text");
			Assert.assertEquals(noResultText, "No filtering results");
		}

		logger.info("Clearing the Search filter");
		// loop through each element
		String manualText = "Manual";
		for (int i = 0; i < manualText.length(); i++) {

			// access each character
			char a = manualText.charAt(i);

			webAppHelper.findElementByXPath("//input[@placeholder='search']").sendKeys(Keys.BACK_SPACE);
		}

	}

	/* TestCaseId - C19660 */
	@Test(enabled = true, priority = 2)
	public void syncJobPaginationTest() {

		logger.info("Getting first Job Id Details ");
		String firstJobId = webAppHelper.findElementsByXPath("//tr//child::td").get(0).getText();

		logger.info("Clicking pagantion forward arrow icon");
		webAppHelper.findElementByXPath("//a[text()='›']").click();

		logger.info("Getting first Job Id Details After click");
		String firstJobIdAfterClick = webAppHelper.findElementsByXPath("//tr//child::td").get(0).getText();

		logger.info("Asserting Job ID before and after clicking arrow icon");
		Assert.assertFalse(firstJobId.contains(firstJobIdAfterClick));

	}

	/* TestCaseId -C19201 */
	@Test(enabled = true, priority = 3)
	public void purchaseOrderCountTest() {

		webAppHelper.scrollPageUp();
		logger.info("Getting Items proccessed");
		List<WebElement> syncRowData = webAppHelper.findElementsByXPath("//table//tr");

		if (syncRowData.size() != 0) {

			logger.info("Getting Items processed for first row from Sync data table");
			String triggerTypetext = syncRowData.get(1).findElements(By.tagName("td")).get(4).getText();

			if (Integer.parseInt(triggerTypetext) == 0) {

				logger.info("Clicking Items processed");
				WebElement itemsProcessedArrowIcon = webAppHelper
						.findElementsByXPath(
								"//*[local-name()='svg' and contains(@class,' CDataTable_arrow-position__NzoQn')]")
						.get(4);
				itemsProcessedArrowIcon.click();
				String itemsProcessedValue = syncRowData.get(1).findElements(By.tagName("td")).get(4).getText();

				logger.info("Getting Job succes count ");
				String successValue = syncRowData.get(0).findElements(By.xpath("//td")).get(5).getText();

				String failValue = syncRowData.get(0).findElements(By.xpath("//td")).get(6).getText();

				int itemsPoCount = Integer.parseInt(itemsProcessedValue);
				int proccessedCount = Integer.parseInt(successValue) + Integer.parseInt(failValue);

				logger.info("Asserting po count");
				Assert.assertEquals(itemsPoCount, proccessedCount);
			}

		}

	}

	/* TestCaseId -C19268,C19270 */
	@Test(enabled = true, priority = 4)
	public void successCountTest() {

		logger.info("Clicking success count value");
		List<WebElement> successPOList = webAppHelper.findElementsByXPath("//table//tbody//tr");
		String triggerTime = successPOList.get(0).findElements(By.tagName("td")).get(1).getText();
		String successMsge = successPOList.get(0).findElements(By.tagName("td")).get(3).getText();

		logger.info("Clicking Job success count");
		// successPOList.get(1).findElements(By.xpath("//td//a")).get(0).click();
		webAppHelper.findElementsByXPath("//table//tr//td//a").get(0).click();

		logger.info("Getting buttons className");
		String tagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocess = webAppHelper.findElementByXPath("//*[text()='Reprocess ']").getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLFailedClassName.contains("disabled"));
		Assert.assertTrue(untagALLFailedClassName.contains("disabled"));
		Assert.assertTrue(reprocess.contains("disabled"));

		logger.info("Getting job trigger time in sync Data");
		String jobTriggerTime = webAppHelper.findElementByXPath("//select[@name='date']//option").getAttribute("value");

		logger.info("Asserting Job Trigger Time");
		Assert.assertEquals(triggerTime, jobTriggerTime);

	}

	/* TestCaseId - C19270 */
	@Test(enabled = true, priority = 5)
	public void SyncedPoUrlTest() {

		logger.info("Clicking success count value");
		List<WebElement> successPOList = webAppHelper.findElementsByXPath("//table//tbody//tr");

		String triggerTime = successPOList.get(0).findElements(By.tagName("td")).get(1).getText();
		logger.info("Getting URL after click on failed count");
		String afterUrl = webAppHelper.getCurrentUrl();
		String triggeredURl = afterUrl.split("=")[1].split("&")[0];
		String passedStatusParam = afterUrl.split("&")[1];
		String accountNameParam = afterUrl.split("&")[2];

		logger.info("Asserting URL ");
		Assert.assertEquals(triggerTime, triggeredURl);
		Assert.assertEquals(passedStatusParam, "status=Success");
		Assert.assertEquals(accountNameParam, "account=WITRE");

	}

	/* TestCaseId - C19269,C19270 */
	@Test(enabled = true, priority = 6)
	public void failedCountTest() {

		webAppHelper.naviagateBack();
		List<WebElement> successPOList = webAppHelper.findElementsByXPath("//table//tbody//tr");
		String triggerTime = successPOList.get(0).findElements(By.tagName("td")).get(1).getText();

		webAppHelper.findElementsByXPath("//table//tr//td//a").get(1).click();
		String tagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocess = webAppHelper.findElementByXPath("//*[text()='Reprocess ']").getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLFailedClassName.contains("btn btn-primary"));
		Assert.assertTrue(untagALLFailedClassName.contains("btn-primary"));
		Assert.assertTrue(reprocess.contains("btn-primary"));

		logger.info("Getting job trigger time in sync Data");
		String jobTriggerTime = webAppHelper.findElementByXPath("//select[@name='date']//option").getAttribute("value");

		logger.info("Asserting Job Trigger Time");
		Assert.assertEquals(triggerTime, jobTriggerTime);

		logger.info("Getting URL after click on failed count");
		String afterUrl = webAppHelper.getCurrentUrl();
		String triggeredURl = afterUrl.split("=")[1].split("&")[0];
		String failedStatusParam = afterUrl.split("&")[1];
		String accountNameParam = afterUrl.split("&")[2];

		logger.info("Asserting URL ");
		Assert.assertEquals(triggerTime, triggeredURl);
		Assert.assertEquals(failedStatusParam, "status=Fail");
		Assert.assertEquals(accountNameParam, "account=WITRE");

	}
 
	@Test(enabled =  true, priority = 7 )
	public void jobIdfilterTest() {

		logger.info("Clicking success count value");
		List<WebElement> poRowsList = webAppHelper.findElementsByXPath("//table//tr");

		if (poRowsList.size() > 2) {

			logger.info("Getting Job Id Details of first row");
			String id = poRowsList.get(1).findElements(By.tagName("td")).get(1).getText();

			logger.info("clicking arrow icon of Job id");
			webAppHelper.findElementByXPath(
					"//*[local-name()='svg' and @class='position-absolute CDataTable_icon-transition__3jEV8 CDataTable_arrow-position__NzoQn']")
					.click();

			logger.info("Getting Job ID after click");
			String jobIdAfterClick = poRowsList.get(1).findElements(By.tagName("td")).get(0).getText();

			logger.info("Asserting Id details");
			Assert.assertNotEquals(id, jobIdAfterClick);

		}

	}
	
	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}
}
