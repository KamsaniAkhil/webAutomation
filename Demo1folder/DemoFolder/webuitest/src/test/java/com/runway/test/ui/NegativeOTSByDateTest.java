package com.runway.test.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.gmail.Gmail.Users.Drafts.List;
import com.runway.test.ui.common.utils.UITestSeleniumHelper;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class NegativeOTSByDateTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("NegativeLoginTest");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "JR365@ims22!@");
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();

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
		return true;
	}

	@Override
	public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		IRetryAnalyzer retry = testannotation.getRetryAnalyzer();

		if (retry == null) {
			testannotation.setRetryAnalyzer(NegativeOTSByDateTest.class);
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

	@Test(enabled = true, priority = 1)

	public void login() {

		webAppHelper.waitForElementToBeLoadedByName("userName");

		logger.info("send username in email component");
		webAppHelper.findElementByName("userName").sendKeys(username);

		logger.info("sending password in password component");
		webAppHelper.findElementByName("password").sendKeys(password);

		logger.info("Clicking Sign In component");
		webAppHelper.findElementByXPath("//*[contains(@class ,'btn-success')]").click();
	}

	@Test(enabled = true, priority = 2)
	public void otsDateTestCheckEmptyDate() {

		webAppHelper.waitForElementToBeLoaded("//*[text()='Reports']");

		logger.info("clicking reports");

		webAppHelper.findElementByXPath("//*[text()='Reports']").click();

		logger.info("clicking Ots By Date ");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

		logger.info("Clicking Include OTS As od Date dropdown icon");
		webAppHelper.findElementByXPath("//select[@class='form-control']").click();

		logger.info("clicking Yes option");
		webAppHelper.findElementByXPath("//option[text()='Yes']").click();

		logger.info("Clicking Include OTS As od Date dropdown icon");
		webAppHelper.findElementByXPath("//select[@class='form-control']").click();

		logger.info("Gettting plus field");
		String symbol = webAppHelper
				.findElementsByXPath("//div[@class='react-datepicker-wrapper']//parent::div//parent::div//parent::div")
				.get(0).getAttribute("class");

		logger.info("Asserting plus field");
		Assert.assertTrue(symbol.contains("mt-3"));

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

	}

	@Test(enabled = true, priority = 3, dependsOnMethods = "otsDateTestCheckEmptyDate")
	public void otsDateEmptyDateFieldAndErrorMessage() {

		logger.info("Getting date calender field box before adding");
		int dateCalender = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count  Date Calender count");
		Assert.assertTrue(dateCalender == 1);

		logger.info("Getting error message text");
		String otsDateError = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker-wrapper']//following-sibling::small").getText();

		logger.info("Asserting OTS Date Error Text");
		Assert.assertEquals(otsDateError, "Fields can't be empty");

	}

	@Test(enabled = true, priority = 4)
	public void runReportWithEmptyDate() {

		webAppHelper.refreshBrowser();
		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.waitSimply(1);
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			logger.info("Getting Run Report Enabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReportAfter.contains("disabled"));

		}

	}

	@Test(enabled = true, priority = 5)
	public void otsDatePastDateTest() {

		logger.info("Getting date calender field box before adding");
		int dateCalenderBefore = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Getting date field component");
		WebElement dateBox1 = webAppHelper.findElementByXPath("//input[@class='form-control']");

		logger.info("Asserting a date in date field textbox");
		dateBox1.sendKeys("11/12/2022");

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count  Date Calender count");
		Assert.assertTrue(dateCalenderBefore == dateCalenderAfter);

	}

	@Test(enabled = true, priority = 6, dependsOnMethods = "otsDatePastDateTest")
	public void otsDatePastDateErrorText() {

		logger.info("Getting error message text");
		String otsDateError = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker-wrapper']//following-sibling::small").getText();

		logger.info("Asserting OTS Date Error Text");
		Assert.assertEquals(otsDateError, "Date must be future date or greater than previous date");

		logger.info("Getting CSS class name for OTS Date field");
		String otsDateCSSText = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input")
				.getAttribute("class");

		logger.info("Asserting OTS Date field css class");
		Assert.assertTrue(otsDateCSSText.contains("border border-danger"));

	}

	@Test(enabled = true, priority = 7, dependsOnMethods = "otsDatePastDateTest")
	public void otsDatefutureDate() {

		logger.info("Getting Run Report Enabled class name");
		String runReportBefore = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReportBefore.contains("disabled")) {

			logger.info("Clicking Include OTS As od Date dropdown icon");
			webAppHelper.findElementByXPath("//select[@class='form-control']").click();

			logger.info("clicking Yes option");
			webAppHelper.waitSimply(1);
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			logger.info("Clicking Date field text box");
			webAppHelper.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input").click();

			logger.info("Clicking arrow icon next month in Date picker");
			WebElement datepickerArrowIcon = webAppHelper
					.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--next')]");
			datepickerArrowIcon.click();
			datepickerArrowIcon.click();
			datepickerArrowIcon.click();

			logger.info("Getting future Day of Month class name");
			webAppHelper.findElementByXPath("//*[text() = '10' ]").click();

			logger.info("Getting Run Report Enabled class name");
			String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReport.contains("enableDiv"));

		}

	}

	@Test(enabled = true, priority = 8, dependsOnMethods = "otsDatefutureDate")
	public void otsDateAddDateFieldFutureDayTest() {

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

		logger.info("Getting date calender field box");
		int dateCalenderBefore = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Checking date calender fieldbox by Clicking Add symbol icon ");
		webAppHelper.findElementsByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

		logger.info("Getting datecalender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count  Date Calender count");
		Assert.assertTrue(dateCalenderBefore == dateCalenderAfter);

		logger.info("Getting error message text");
		String otsDateError = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker-wrapper']//following-sibling::small").getText();

		logger.info("Asserting OTS Date Error Text");
		Assert.assertEquals(otsDateError, "Fields can't be empty");

	}

	@Test(enabled = true, priority = 9)
	public void addedDateFieldTest() {

		logger.info("Getting date field component");
		WebElement dateBox = webAppHelper.findElementsByXPath("//input[contains(@class,'form-control')]").get(1);

		logger.info("Clicking date field text box");
		dateBox.click();

		LocalDate currentdate = LocalDate.now();

		int currentYear = currentdate.getYear();

		int currentMonth = currentdate.getMonthValue();

		logger.info("Sending selected date in next date field textbox");
		dateBox.sendKeys((currentMonth) + "/10/" + currentYear);

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementsByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

		logger.info("Getting error message text");
		String otsDateErrorText = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker-wrapper']//following-sibling::small").getText();

		logger.info("Asserting OTS Date Error Text");
		Assert.assertEquals(otsDateErrorText, "Date must be future date or greater than previous date");

	}

	@Test(enabled = true, priority = 10, dependsOnMethods = "addedDateFieldTest")
	public void otsDatePresentDate() {

		LocalDate currentdate = LocalDate.now();

		String presentDate = currentdate.format(DateTimeFormatter.ofPattern("MM/d/YYYY"));

		webAppHelper.refreshBrowser();
		logger.info("clicking Yes option");
		webAppHelper.waitSimply(1);
		webAppHelper.findElementByXPath("//option[text()='Yes']").click();

		logger.info("Getting date field component");
		WebElement dateBox1 = webAppHelper.findElementByXPath("//input[@class='form-control']");

		logger.info("Asserting a date in date field textbox");
		dateBox1.sendKeys("" + presentDate + "");

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderBefore = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		webAppHelper.findElementsByXPath("//input[@class='form-control']").get(1).sendKeys("" + presentDate + "");

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count Date Calender count");
		Assert.assertTrue(dateCalenderBefore == dateCalenderAfter);

		logger.info("Getting Run Report  class name");
		String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		Assert.assertTrue(runReportAfter.contains("disabled"));

	}

	@Test(enabled = true, priority = 11, dependsOnMethods = "otsDatePresentDate")
	public void runReportWithSameDate() {

		webAppHelper.refreshBrowser();
		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			logger.info("Getting date field component");
			WebElement dateBox = webAppHelper.findElementByXPath("//input[@class='form-control']");

			logger.info("Asserting a date in date field textbox");
			dateBox.sendKeys("08/21/2023");

			logger.info("Adding date calender fieldbox");
			webAppHelper.findElementByXPath("//*[local-name()='svg' and (@data-icon='plus')]").click();

			logger.info("Getting date field component");
			WebElement dateBox1 = webAppHelper.findElementsByXPath("//input[@class='form-control']").get(1);

			logger.info("Asserting a date in date field textbox");
			dateBox1.sendKeys("08/21/2023");
			logger.info("Adding date calender fieldbox");
			webAppHelper.findElementsByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

			logger.info("Getting error message text");
			String otsDateErrorText = webAppHelper
					.findElementByXPath("//div[@class='react-datepicker-wrapper']//following-sibling::small").getText();

			logger.info("Asserting OTS Date Error Text");
			Assert.assertEquals(otsDateErrorText, "Date must be future date or greater than previous date");

			logger.info("Adding date calender fieldbox");
			webAppHelper.findElementsByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

			logger.info("Getting Run Report Disabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

			logger.info("Asserting Run Report Disabled class name ");
			Assert.assertTrue(runReportAfter.contains("disabled"));

		}

	}

	@Test(enabled = true, priority = 12, dependsOnMethods = "runReportWithSameDate")
	public void runReportWithPastDate() {

		webAppHelper.refreshBrowser();
		webAppHelper.waitSimply(2);

		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			webAppHelper.scrollPageUp();
			logger.info("Clicking Date field text box");
			webAppHelper.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input").click();

			logger.info("Clicking arrow icon next month in Date picker");
			webAppHelper.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--next')]")
					.click();

			logger.info("Getting A Day of Month class name");
			webAppHelper.findElementByXPath("//*[text() = '15' ]").click();
			webAppHelper.waitSimply(1);

			logger.info("Adding Date field box");
			webAppHelper.findElementByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").click();

			logger.info("Getting date field component");
			WebElement dateBox1 = webAppHelper.findElementsByXPath("//input[contains(@class,'form-control')]").get(1);

			logger.info("Asserting a date in date field textbox");
			dateBox1.sendKeys("03/19/2021");

			logger.info("Getting Run Report Disabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReportAfter.contains("disabled"));

			logger.info("Adding Date field box");
			webAppHelper.findElementsByXPath(" //*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

			logger.info("Getting Run Report Enabled class name");
			String runReportPastDate = webAppHelper.findElementByXPath("//*[text()='Run Report']")
					.getAttribute("class");

			logger.info("Asserting Run Report class name ");
			Assert.assertTrue(runReportPastDate.contains("disabled"));

		}

	}

	@Test(enabled = true, priority = 13, dependsOnMethods = "runReportWithPastDate")
	public void runReportWithPastDateField() {

		webAppHelper.refreshBrowser();
		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			webAppHelper.scrollPageUp();
			logger.info("Getting date field component");
			WebElement dateBox1 = webAppHelper.findElementByXPath("//input[@class='form-control']");

			logger.info("Asserting a date in date field textbox");
			dateBox1.sendKeys("08/21/2022");

			logger.info("clicking include WIP  checkbox");

			webAppHelper.findElementsByXPath("//label[contains(@for,'WIP')]").get(0).click();

			logger.info("clicking Ignore Stock When Below Zero  checkbox");
			webAppHelper.findElementByXPath("//label[contains(@for,'Ignore')]").click();

			logger.info("selecting Display Stock by Specified Warehouse");
			webAppHelper.findElementByXPath("//label[contains(@for,'Display')]").click();

			logger.info("Selecting Tag All button");
			webAppHelper.findElementByXPath("//*[text()='Tag All']").click();

			webAppHelper.scrollPageDown();
			logger.info("Selecting OTS By Size");
			webAppHelper.findElementByXPath("//*[text()='Display OTS by Size?']").click();

			logger.info("Getting Run Report Enabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

			logger.info("Asserting Run Report  class name ");
			Assert.assertTrue(runReportAfter.contains("disabled"));

			logger.info("clicking No option");
			webAppHelper.findElementByXPath("//option[text()='No']").click();

			logger.info("Getting Run Report Enabled class name");
			String runReportNoOption = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReportNoOption.contains("enableDiv"));

		}
	}

	@Test(enabled = true, priority = 14, dependsOnMethods = "runReportWithPastDateField")
	public void runReportDatePresentDate() {

		webAppHelper.refreshBrowser();
		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.waitSimply(1);
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			webAppHelper.scrollPageUp();
			logger.info("Selecting date field textbox");
			webAppHelper.findElementByXPath("//input[contains(@class,'form-control')]").click();

			logger.info("Selecting arrow icon in datepicker");
			webAppHelper.findElementByXPath("//*[contains(@class,'react-datepicker__navigation--next')]").click();

			logger.info("Selecting a Date");
			webAppHelper.findElementByXPath("//div[text()='15']").click();

			webAppHelper.scrollPageDown();
			webAppHelper.findElementByXPath("//*[text()='Display OTS by Size?']").click();

			logger.info("Getting Run Report Enabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReportAfter.contains("enableDiv"));

		}

	}

	@Test(enabled = true, priority = 15, dependsOnMethods = "runReportDatePresentDate")
	public void runReportDateNoOption() {

		webAppHelper.refreshBrowser();
		webAppHelper.scrollPageDown();
		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Yes option");
			webAppHelper.findElementByXPath("//option[text()='Yes']").click();

			logger.info("clicking No option");
			webAppHelper.findElementByXPath("//option[@value='No']").click();

			webAppHelper.scrollPageDown();
			logger.info("Selecting OTS By Size");
			webAppHelper.findElementByXPath("//*[text()='Display OTS by Size?']").click();

			logger.info("Getting Run Report Enabled class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");

			logger.info("Asserting Run Report Enabled class name ");
			Assert.assertTrue(runReportAfter.contains("enableDiv"));

		}
	}

	@AfterClass
	public void unsetup() {
		webAppHelper.quitBrowser();
	}
}
