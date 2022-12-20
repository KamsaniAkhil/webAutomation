package com.runway.test.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

public class SyncSettingsTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("SyncJobs");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "anthony@aims360.com");
	public static String password = System.getProperty("password", "Witre12!");

	WebDriver driver;
	private String countNotification = null;
	private String settingsToken = System.getProperty("mondaycomToken",
			"eyJhbGciOiJIUzI1NiJ9.eyJ0aWQiOjE3Njc0MzA0NCwidWlkIjozMzMwMzU4MSwiaWFkIjoiMjAyMi0wOC0yMlQxMToxOToxNC44ODZaIiwicGVyIjoibWU6d3JpdGUiLCJhY3RpZCI6ODAxOTYzOCwicmduIjoidXNlMSJ9.4s1PdcIU209INFRG1NnZgzr2QhmFXWdKIcWmCb4YNdg");
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

	@Test(enabled = true, priority = 1)
	public void settingsUITest() {
         
		webAppHelper.waitForElementToBeLoadedByName("userName");
		webAppHelper.loginPage(username, password);

		logger.info("Clicking Sync Jobs");
		webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(2).click();
		webAppHelper.findElementByXPath("//a[text()='Settings']").click();

		logger.info("Getting header title names");
		String headerTitleName = webAppHelper.findElementByXPath("//li[@class='breadcrumb-item']").getText();
		String settingsheaderTitle = webAppHelper.findElementsByXPath("//li[contains(@class,'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting Header title name's");
		Assert.assertEquals(headerTitleName, "Monday.com");
		Assert.assertEquals(settingsheaderTitle, "Settings");

		logger.info("Getting client account name");
		String clientName = webAppHelper.findElementByXPath("//a[contains(@class,'c-subheader-nav-link')]").getText();

		logger.info("getting account param in current URL ");
		String acountparam = webAppHelper.getCurrentUrl();
		String acountName = acountparam.split("=")[1];

		logger.info("Asserting account name");
		Assert.assertEquals(acountName, clientName.trim());

		logger.info("Getting card title name and asserting title name");
		String cardTitle = webAppHelper.findElementByClassName("card-header").getText();
		Assert.assertEquals(cardTitle, "Settings");

	}

	@Test(enabled = true, priority = 2)
	public void settingsUITest1() {

		logger.info("Getting token label name");
		String tokenName = webAppHelper.findElementByXPath("//label[@for='Token']").getText();

		logger.info("ASserting Token lable name");
		Assert.assertEquals(tokenName, "*Token");

		logger.info("Getting board label name");
		String boardName = webAppHelper.findElementByXPath("//label[@for='Board']").getText();

		logger.info("ASserting Board lable name");
		Assert.assertEquals(boardName, "*Board");

		logger.info("Getting Group label name");
		List<WebElement> lableList = webAppHelper.findElementsByClassName("col-md-3");
		List<WebElement> lableList1 = webAppHelper.findElementsByClassName("col-md-2");

		logger.info("ASserting Group lable name");
		Assert.assertEquals(lableList.get(2).getText(), "*Group");

		logger.info("ASserting Interval lable name");
		Assert.assertEquals(lableList1.get(0).getText(), "*Interval");

		logger.info("ASserting Frequency lable name");
		Assert.assertEquals(lableList1.get(1).getText(), "*Frequency");

		logger.info("ASserting Hours lable name");
		Assert.assertEquals(lableList.get(3).getText(), "*Hours");

		logger.info("ASserting Minutes lable name");
		Assert.assertEquals(lableList.get(5).getText(), "*Minutes");

		logger.info("Getting save button Name");
		String saveButton = webAppHelper.findElementByXPath("//div[contains(@class,' col-md-3')]").getText().trim();

		logger.info("Asserting save button name");
		Assert.assertEquals(saveButton, "Save");

	}

	/* TestCaseId-C19287 */
	@Test(enabled = true, priority = 3)
	public void settingsPageForNewUser() {

		logger.info("Gettings token value");
		String tokenValue = webAppHelper.findElementByXPath("//input[@name='Token']").getAttribute("value");

		if (tokenValue.equals("")) {

			logger.info("Gettings save button class name");
			String saveButton = webAppHelper.findElementByXPath("//*[text()='Save ']").getAttribute("class");

			logger.info("Asserting save button class name");
			Assert.assertTrue(saveButton.contains("disabled"));
			labelDisabledValue();
		}
	}

	public void labelDisabledValue() {

		logger.info("Gettings and Asserting Default board options");
		boolean boardDisableValue = webAppHelper.findElementByXPath("//select[@name='Board']").isEnabled();

		Assert.assertEquals(boardDisableValue, false);

		logger.info("Gettings and Asserting Default Group options");
		boolean disabledGroupValue = webAppHelper.findElementByXPath("//select[@name='Group']").isEnabled();

		Assert.assertEquals(disabledGroupValue, false);

		logger.info("Gettings and Asserting Default frequency options");
		boolean disabledfreqValue = webAppHelper.findElementByXPath("//select[@name='frequency']//option").isEnabled();

		Assert.assertEquals(disabledfreqValue, false);

	}

	/* TestCaseId-C19289 */
	@Test(enabled = true, priority = 4)
	public void validToken() {

		logger.info("Gettings token value");
		WebElement tokenValue = webAppHelper.findElementByXPath("//input[@name='Token']");// .getAttribute("value");

		sendToken();
	}

	/* TestCaseId-C19337 */
	@Test(enabled = true, priority = 5)
	public void settingsPageForValidToken() {

		logger.info("Selecting Board dropdown option");
		WebElement board = webAppHelper.findElementByXPath("//select[@name='Board']");// .click();
		board.click();
		webAppHelper.findElementByXPath("//option[text()='Nex-Aims Test']").click();
		board.click();

		logger.info("selecting Group dropdown option");
		WebElement group = webAppHelper.findElementByXPath("//select[@name='Group']");// .click();
		group.click();
		List<WebElement> groupOptionList = webAppHelper.findElementsByXPath("//select[@name='Group']//option");
		groupOptionList.get(2).click();
		group.click();

		logger.info("Sending Interval value");
		WebElement intervalValue = webAppHelper.findElementByXPath("//input[@name='interval']");
		intervalValue.clear();
		intervalValue.sendKeys("1");

		logger.info("Seleting frequency dropdown option");
		WebElement freqDropdown = webAppHelper.findElementByXPath("//select[@name='frequency']");
		freqDropdown.click();
		webAppHelper.findElementByXPath("//option[@value='Day']").click();
		freqDropdown.click();

		logger.info("Getting Hours and Minutes dropdown option icon");
		List<WebElement> hourAndMinuteDropdown = webAppHelper
				.findElementsByXPath("//*[local-name()='svg' and @class='dropdown-heading-dropdown-arrow gray']");

		List<WebElement> hoursAndMinuteText = webAppHelper.findElementsByClassName("dropdown-heading-value");

		if (hoursAndMinuteText.get(0).getText().contains("Select")) {

			logger.info("selecting hour dropdown option");
			hourAndMinuteDropdown.get(0).click();

			WebElement selectAllOption = webAppHelper.findElementByXPath("//*[text()='Select All']");
			selectAllOption.click();

		}

		List<WebElement> closeWindow = webAppHelper.findElementsByClassName("dropdown-heading-value");
		closeWindow.get(0).click();

		if (hoursAndMinuteText.get(1).getText().contains("Select")) {

			logger.info("selecting minutes dropdown");
			hourAndMinuteDropdown.get(1).click();
		} else {

			webAppHelper.findElementsByXPath("//button[@class='clear-selected-button']").get(1).click();
		}

		logger.info("Getting Minutes dropdown option list");
		hourAndMinuteDropdown.get(1).click();
		List<WebElement> minutesDropdown = webAppHelper.findElementsByXPath("//label[@class='select-item ']");

		logger.info("selecting minutes dropdown option");

		minutesDropdown.get(1).click();
		closeWindow.get(1).click();

		logger.info("Getting preview data");
		List<WebElement> previewInfo = webAppHelper.findElementsByXPath("//p[@class='m-0'][2]//child::span");

		logger.info("Asserting preview size");
		Assert.assertEquals(previewInfo.size(), 24);

		logger.info("Clicking save button");
		webAppHelper.findElementByXPath("//button[text()='Save ']").click();
	}

	/* TestCaseId-C19288 */
	@Test(enabled = false, priority = 6)
	public void defaultSaveButton() {
		webAppHelper.refreshBrowser();
		logger.info("Getting token value");
		WebElement tokenTextBox = webAppHelper.findElementByXPath("//input[@name='Token']");
		String tokenValue = tokenTextBox.getAttribute("value");

		TokenValidation(tokenValue);
		sendToken();
		TokenValidation(tokenValue);
		// write here save enable condn

	}

	public void sendToken() {

		WebElement tokenTextBox = webAppHelper.findElementByXPath("//input[@name='Token']");
		logger.info("Sending Token");
		tokenTextBox.sendKeys(settingsToken);
		tokenTextBox.sendKeys(Keys.TAB);

	}

	public void TokenValidation(String tokenValue) {

		if (tokenValue.equals("")) {

			logger.info("Getting save button disabled classname");
			String saveButton = webAppHelper.findElementByXPath("//*[text()='Save ']").getAttribute("class");

			logger.info("Asserting save button class name");
			Assert.assertTrue(saveButton.contains("disabled"));
		}
	}

	@Test(enabled = true, priority = 7)
	public void removeTokenTest() {

		logger.info("Getting token component");
		WebElement tokenTextBox = webAppHelper.findElementByXPath("//input[@name='Token']");
		String tokenValue = tokenTextBox.getAttribute("value");

		if (tokenValue.equals("")) {

			sendToken();
			webAppHelper.findElementByXPath("//input[@name='Token']").clear();
			String saveButton = webAppHelper.findElementByXPath("//button[text()='Save ']").getAttribute("class");
			Assert.assertTrue(saveButton.contains("disabled"));

			logger.info("Getting Helper message in the settings page");
			String helperMsge = webAppHelper.findElementByXPath("//p[contains(@class,'text-warning')]").getText();
			Assert.assertEquals(helperMsge,
					"All fields are mandatory, Please select all the fields to save settings .");

		}

	}
	
	@AfterClass
	public void unsetUp() {
		webAppHelper.quitBrowser();
	}
}
