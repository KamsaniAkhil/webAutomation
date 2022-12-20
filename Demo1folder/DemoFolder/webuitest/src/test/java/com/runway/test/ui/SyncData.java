package com.runway.test.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Array;
import java.util.List;

import org.openqa.selenium.By;
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

@Listeners(com.runway.test.ui.common.utils.TestNGCustomResults.class)
public class SyncData implements IRetryAnalyzer, IAnnotationTransformer {

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

	/* TestCaseId-C19274 */
	@Test(enabled = true, priority = 1)
	public void syncDataUITest() {

		webAppHelper.loginPage(username, password);

		logger.info("Clicking Sync Jobs");
		webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(2).click();
		webAppHelper.findElementByXPath("//a[text()='Sync Data']").click();

		logger.info("Getting header title names");
		String headerTitleName = webAppHelper.findElementByXPath("//li[@class='breadcrumb-item']").getText();
		String syncDataheaderTitle = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting Header title name's");
		Assert.assertEquals(headerTitleName, "Monday.com");
		Assert.assertEquals(syncDataheaderTitle, "SyncData");

		logger.info("Getting and asserting fiter By lable name");
		String filterByName = webAppHelper.findElementByXPath("//label[@class='mt-4']").getText();
		Assert.assertEquals(filterByName, "Filter By");

		logger.info("Getting triggere time label name");
		String triggerTime = webAppHelper.findElementByXPath("//div[@class='col-md-5']//child::label").getText();
		Assert.assertEquals(triggerTime, "Job Triggered Time");

		logger.info("Getting status label name");
		String status = webAppHelper.findElementsByXPath("//div[@class='col-md-5']//child::label").get(1).getText();
		Assert.assertEquals(status, "Status");

		logger.info("Clicking status dropdown option");
		WebElement options = webAppHelper.findElementByXPath("//select[@name='status']");
		webAppHelper.findElementByXPath("//select[@name='status']").click();

		List<WebElement> optionsList = options.findElements(By.tagName("option"));
		String allOptionName = optionsList.get(0).getText();
		String successOptions = optionsList.get(1).getText();
		String failedOptions = optionsList.get(2).getText();

		logger.info("Asserting options List");
		Assert.assertEquals(allOptionName, "All");
		Assert.assertEquals(successOptions, "Success");
		Assert.assertEquals(failedOptions, "Failed");

		webAppHelper.findElementByXPath("//select[@name='status']").click();

		logger.info("Getting buttons className");
		String tagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLFailedClassName = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocess = webAppHelper.findElementByXPath("//*[text()='Reprocess ']").getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLFailedClassName.contains("btn-primary"));
		Assert.assertTrue(untagALLFailedClassName.contains("btn-primary"));
		Assert.assertTrue(reprocess.contains("btn-primary"));

		logger.info("Getting label names for search and show");
		List<WebElement> listLabelName = webAppHelper.findElementsByXPath("//label[@class='mfe-2']");
		String searchName = listLabelName.get(0).getText();
		String showLabel = listLabelName.get(1).getText();

		logger.info("Asserting label names");
		Assert.assertEquals(searchName, "Search:");
		Assert.assertEquals(showLabel, "Show:");

	}

	@Test(enabled = true, priority = 2)
	public void syncDataLableTest() {

		List<WebElement> tabelHeaderName = webAppHelper.findElementsByXPath("//table//thead//tr//th");

		String headers[] = new String[9];

		for (int i = 0; i <= headers.length - 1; i++) {

			headers[i] = tabelHeaderName.get(i).getText();
			System.out.println(headers[i]);
		}
		Assert.assertEquals(headers[0], "Select");
		Assert.assertEquals(headers[1], "Po No");
		Assert.assertEquals(headers[2], "Processed Time");
		Assert.assertEquals(headers[3], "Vendor Code");
		Assert.assertEquals(headers[4], "Style");
		Assert.assertEquals(headers[5], "Color");
		Assert.assertEquals(headers[6], "Job Id");
		Assert.assertEquals(headers[7], "Status");
		Assert.assertEquals(headers[8], "Message");

	}

	@Test(enabled = true, priority = 3)
	public void syncStatus() {

		logger.info("Clicking Status dropdown");
		webAppHelper.findElementByXPath("//select[@name='status']").click();

		WebElement options = webAppHelper.findElementByXPath("//select[@name='status']");
		List<WebElement> optionsList = options.findElements(By.tagName("option"));

		optionsList.get(1).click();
		webAppHelper.findElementByXPath("//*[text()='Message']").click();

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

		optionsList.get(2).click();
		webAppHelper.findElementByXPath("//*[text()='Message']").click();

		logger.info("Getting buttons className");
		String tagALLForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocessForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Reprocess ']")
				.getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLForFailedStatus.contains("btn-primary"));
		Assert.assertTrue(untagALLForFailedStatus.contains("btn-primary"));
		Assert.assertTrue(reprocessForFailedStatus.contains("btn-primary"));

	}

	/* TestCaseId-C19277 */
	@Test(enabled = true, priority = 4)
	public void jobTriggeredTimeTest() {

		logger.info("Getting Job ");
		webAppHelper.findElementByXPath("//select[@name='date']").click();

		logger.info("Getting No filtering results text");
		String noResultText = webAppHelper.findElementByXPath("//div[@class='setselect']").getText();

//		logger.info("Getting triggered time dropdown options");
//		List<WebElement> trigerredTimeList = webAppHelper.findElementsByXPath("//select[@name='date']//option");
//		
//		logger.info("Getting table aync data rows");
//		List<WebElement> syncDateRows = webAppHelper.findElementsByXPath("//table//tr");
//		
		logger.info("Asserting no filtering text");
		if (noResultText.contains("No items")) {

			webAppHelper.findElementsByXPath("//select[@name='date']//option").get(1).click();
			logger.info("Getting triggered time Date options count");
			int optionCount = webAppHelper.findElementsByXPath("//select[@name='date']//option").size();
			webAppHelper.findElementByXPath("//*[text()='Status']").click();

			for (int i = 1; i <= optionCount; i++) {

				logger.info("Clicking Triggered time select dropdown option");
				webAppHelper.findElementsByXPath("//select[@name='date']//option").get(i).click();
				webAppHelper.findElementByXPath("//*[text()='Status']").click();

				logger.info("Getting table sync data rows");
				List<WebElement> syncDateRowsAfterClick = webAppHelper.findElementsByXPath("//table//tr");

				if (syncDateRowsAfterClick.size() > 2) {

					System.out.println("succeess");

					break;
				}

			}
		}

	}

	/* TestCaseId-C19280 */
	@Test(enabled = true, priority = 5)
	public void syncDataSuccessStatusTest() {

		String defaultStatus = webAppHelper.findElementByXPath("//select[@name='status']").getText();
		logger.info("Getting buttons className");
		String tagALLForDefaultStatus = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLForDefaultStatus = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocessForDefaultStatus = webAppHelper.findElementByXPath("//*[text()='Reprocess ']")
				.getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLForDefaultStatus.contains("btn-primary"));
		Assert.assertTrue(untagALLForDefaultStatus.contains("btn-primary"));
		Assert.assertTrue(reprocessForDefaultStatus.contains("btn-primary"));

		logger.info("Clicking success option in Status dropdown");
		webAppHelper.findElementsByXPath("//select[@name='status']//option").get(1).click();

		logger.info("Getting buttons classname for success status");
		String tagALLForSuccessStatus = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLForSuccessStatus = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocessForSuccessStatus = webAppHelper.findElementByXPath("//*[text()='Reprocess ']")
				.getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLForSuccessStatus.contains("disabled"));
		Assert.assertTrue(untagALLForSuccessStatus.contains("disabled"));
		Assert.assertTrue(reprocessForSuccessStatus.contains("disabled"));

	}

	/* TestCaseId-C19281 */
	@Test(enabled = true, priority = 6)
	public void syncDataFailedStatusTest() {

		logger.info("Getting buttons className");
		String tagALLStatus = webAppHelper.findElementByXPath("//*[text()='Tag all failed']").getAttribute("class");
		String untagALLStatus = webAppHelper.findElementByXPath("//*[text()='Untag all failed']").getAttribute("class");
		String reprocessStatus = webAppHelper.findElementByXPath("//*[text()='Reprocess ']").getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLStatus.contains("disabled"));
		Assert.assertTrue(untagALLStatus.contains("disabled"));
		Assert.assertTrue(reprocessStatus.contains("disabled"));

		logger.info("Clicking success option in Status dropdown");
		webAppHelper.findElementsByXPath("//select[@name='status']//option").get(2).click();

		logger.info("Getting buttons classname for success status");
		String tagALLForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Tag all failed']")
				.getAttribute("class");
		String untagALLForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Untag all failed']")
				.getAttribute("class");
		String reprocessForFailedStatus = webAppHelper.findElementByXPath("//*[text()='Reprocess ']")
				.getAttribute("class");

		logger.info("Asserting Sync Data buttons className");
		Assert.assertTrue(tagALLForFailedStatus.contains("btn-primary"));
		Assert.assertTrue(untagALLForFailedStatus.contains("btn-primary"));
		Assert.assertTrue(reprocessForFailedStatus.contains("btn-primary"));

	}

	/* TestCaseId-C19282 */
	@Test(enabled = true, priority = 7)
	public void reprocessTestForFailedNoPODetails() {

		logger.info("Getting Table rows");
		List<WebElement> syncDateRows = webAppHelper.findElementsByXPath("//table//tr");

		logger.info("Getting No filtering results text");
		String noResultText = webAppHelper.findElementByXPath("//div[@class='setselect']").getText();
		logger.info("Asserting no filtering text");
		if (noResultText.contains("No items")) {

			logger.info("Clicking Reprocess button");
			webAppHelper.findElementByXPath("//*[text()='Reprocess ']").click();

			logger.info("Getting alert window title name");
			String alertTitleName = webAppHelper.findElementByXPath("//div[@class='modal-content']//header").getText();

			logger.info("Getting ALert message");
			String alertMsge = webAppHelper.findElementByClassName("modal-body").getText();

			logger.info("Clicking to close alert window");
			webAppHelper.findElementByClassName("modal-footer").click();

		}

	}

	@Test(enabled = true, priority = 8)
	public void reprocessForFailedJobs() {

		logger.info("Getting No filtering results text");
		String noResultText = webAppHelper.findElementByXPath("//div[@class='setselect']").getText();
		logger.info("Asserting no filtering text");
		if (noResultText.equalsIgnoreCase("No items")) {

			logger.info("Getting Table rows");
			List<WebElement> syncDateRows = webAppHelper.findElementsByXPath("//table//tr");
			syncDateRows.get(1).findElement(By.tagName("input")).click();

			logger.info("Clicking Untag all failed job");
			webAppHelper.findElementByXPath("//*[text()='Untag all failed']").click();
			logger.info("Clicking tag all failed job");
			webAppHelper.findElementByXPath("//*[text()='Tag all failed']").click();
			webAppHelper.findElementByXPath("//*[text()='Reprocess ']");// .click();

		}

	}

	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}
}
