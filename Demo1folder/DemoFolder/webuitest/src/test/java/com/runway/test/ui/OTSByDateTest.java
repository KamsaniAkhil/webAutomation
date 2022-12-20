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
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.CSVReader;
import com.runway.test.ui.common.utils.UITestSeleniumHelper;

@Listeners({ com.runway.test.ui.common.utils.TestNGCustomResults.class })

public class OTSByDateTest implements IRetryAnalyzer, IAnnotationTransformer {

	Logger logger = LoggerFactory.getLogger("OTSByDateTest");

	public static String currentRunningMethodName = "";
	private int retryCount = 0;
	private int maxRetryCount = 1;
	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "JR365@ims22!@");

	private String countNotification = null;
	// Helper classes
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();

	DashboardTest newdashboard = new DashboardTest();
	public static WebDriver driver;
	Calendar cal = Calendar.getInstance();

	LocalDate currentdate = LocalDate.now();

	// Getting current month name
	Month currentMonth = currentdate.getMonth();

	// Getting current year
	int currentYear = currentdate.getYear();

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

	@Test(enabled = false, priority = 1)
	public void NavigateToReports() {

		webAppHelper.waitForElementToBeLoadedByClassName("userName");
		// webAppHelper.loginPage(username, password);
		webAppHelper.findElementByName("userName").sendKeys(username);
		webAppHelper.findElementByName("password").sendKeys(password);
		webAppHelper.findElementByXPath("//button[contains(@class,'btn-success')]").click();
		logger.info("clicking to open reports");
		WebElement reports = webAppHelper.waitForElementToBeClickable("//*[text()='Reports']");
		reports.click();

		logger.info("clicking Ots By Date ");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

	}

	@Test(enabled = true, priority = 2) // , dependsOnMethods = "NavigateToReports")
	public void navigateToOTSbyDate() {

		webAppHelper.waitForElementToBeLoadedByName("userName");
		webAppHelper.findElementByName("userName").sendKeys(username);
		webAppHelper.findElementByName("password").sendKeys(password);
		webAppHelper.findElementByXPath("//button[contains(@class,'btn-success')]").click();

		logger.info("clicking to open reports");
		WebElement reports = webAppHelper.waitForElementToBeClickable("//*[text()='Reports']");
		reports.click();

		logger.info("clicking Ots By Date ");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

		logger.info("getting header title name OTS Collection");
		String otsCollection = webAppHelper.findElementsByClassName("card-header").get(0).findElement(By.tagName("h6"))
				.getText();

		logger.info("Asserting OTS Collection card");
		Assert.assertEquals(otsCollection, "OTS Calculation");

		logger.info("getting header title name OTS Date card");
		String otsdate = webAppHelper.findElementsByClassName("card-header").get(1).findElement(By.tagName("h6"))
				.getText();

		logger.info("Asserting  OTS Date card");
		Assert.assertEquals(otsdate, "OTS Date");

		logger.info("getting header title name Warehouse card");
		String warehouse = webAppHelper.findElementsByClassName("card-header").get(2).findElement(By.tagName("h6"))
				.getText();

		logger.info("Asserting Warehouse card");
		Assert.assertEquals(warehouse, "Warehouse");

	}

	@Test(enabled = true, priority = 3, dependsOnMethods = "navigateToOTSbyDate")
	public void sidebarMenuOptionAndLogoTest() {

		logger.info("Getting sidebar WMS name");
		String sidebarWMSText = webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(1).getText();
		String sideBarReportsText = webAppHelper.findElementsByClassName("c-sidebar-nav-dropdown-toggle").get(0)
				.getText();

		logger.info("Asserting Side bar text");
		Assert.assertEquals(sidebarWMSText, "WMS");
		Assert.assertEquals(sideBarReportsText, "Reports");

		logger.info("Getting icon attribute in Side Bar");
		String reportsIcon = webAppHelper
				.findElementsByXPath("//*[local-name()='svg' and (@class='c-sidebar-nav-icon')]").get(1)
				.getAttribute("xmlns");
		String wmsIcon = webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@class='c-sidebar-nav-icon')]")
				.get(2).getAttribute("xmlns");

		logger.info("Asserting  Icons in Side Bar");
		Assert.assertTrue(reportsIcon.contains("http://www.w3.org/2000/svg"));
		Assert.assertEquals(wmsIcon, "http://www.w3.org/2000/svg");

		logger.info("Getting logo src name");
		String logoSrcname = webAppHelper.findElementsByXPath("//a[contains(@class,'c-sidebar-brand')]//child::img")
				.get(0).getAttribute("src");

		logger.info("Asserting Logo");
		Assert.assertTrue(logoSrcname.contains("logo-light.e5cf5536e0c7f64383bb.png"));

	}

	@Test(enabled = true, priority = 4, dependsOnMethods = "sidebarMenuOptionAndLogoTest")
	public void NavigationToReports() {

		logger.info("Clicking Reports name in Header");
		webAppHelper.findElementsByXPath("//li[contains(@class , 'breadcrumb-item')]").get(0).click();

		logger.info("Getting Helper message");
		String reportsMsge = webAppHelper.findElementByClassName("welcomeMessage").getText();

		logger.info("Asserting helper message in the Reports");
		Assert.assertEquals(reportsMsge, "Please select an item from left menu to get started.");

		logger.info("clicking Ots By Date ");
		webAppHelper.findElementByXPath("//a[text()='OTS By Date']").click();

		webAppHelper.refreshBrowser();
		logger.info("Getting sidebar WMS name");
		String sideBarMenuName = webAppHelper.findElementByXPath("//ul[contains(@class,'c-sidebar-nav ')]").getText();

		logger.info("Asserting Side bar text");

		Assert.assertTrue(sideBarMenuName.contains("Dashboard"));
		Assert.assertTrue(sideBarMenuName.contains("WMS"));
		Assert.assertTrue(sideBarMenuName.contains("Reports"));

	}

	@Test(enabled = true, priority = 5, dependsOnMethods = "NavigationToReports")
	public void headerNameAndClientIdOTSbyDate() {

		logger.info("Getting OTS By Date header title name");
		String otsByDateText = webAppHelper.findElementsByXPath("//li[contains(@class , 'breadcrumb-item')]").get(1)
				.getText();

		logger.info("Asserting OTS By Date title name");
		Assert.assertEquals(otsByDateText, "OTS By Date");

		logger.info("Getting Client Id");
		String clientId = webAppHelper.findElementByXPath("//a[contains(@class,'c-subheader-nav-link')]").getText();

		logger.info("Asserting Client Id name");
		Assert.assertTrue(clientId.contains("JR365"));
	}

	@Test(enabled = true, priority = 5)
	public void sidebarMinimizer() {

		logger.info("Getting Sidebar css class name before click");

		String sidebarMiniMizerBefore = webAppHelper
				.findElementByXPath("//a[contains(@class , 'c-sidebar-brand ')]//parent::div").getAttribute("class");

		logger.info("Asserting sidebar css class name before click");
		Assert.assertTrue(sidebarMiniMizerBefore.contains(" c-sidebar-fixed"));

		logger.info("Clicking Side bar Minimizer");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-sidebar-minimizer')]").click();

	}

	@Test(enabled = true, priority = 6, dependsOnMethods = "sidebarMinimizer")
	public void sidebarMinimizerTest() {

		logger.info("Getting Sidebar css class name after click");

		String sidebarMiniMizerAfter = webAppHelper
				.findElementByXPath("//a[contains(@class , 'c-sidebar-brand ')]//parent::div").getAttribute("class");

		logger.info("Asserting sidebar css class name After click");
		Assert.assertTrue(sidebarMiniMizerAfter.contains(" c-sidebar-unfoldable"));

		logger.info("Clicking Side bar Minimizer");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-sidebar-minimizer')]").click();

	}

	@Test(enabled = true, priority = 7, dependsOnMethods = "sidebarMinimizerTest")
	public void sidebarMinimizercssTest() {

		logger.info("Getting Sidebar fixed css class name");

		String sidebarMiniMizerBefore = webAppHelper
				.findElementByXPath("//a[contains(@class , 'c-sidebar-brand ')]//parent::div").getAttribute("class");

		logger.info("Asserting sidebar css class ");
		Assert.assertTrue(sidebarMiniMizerBefore.contains(" c-sidebar-fixed"));

		logger.info("Verifying Side bar position");
		webAppHelper.findElementByClassName("c-subheader-nav-link").click();

	}

	@Test(enabled = true, priority = 8)
	public void notificationHeaderTest() {

		logger.info("clicking notification icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-item ')]").click();
		webAppHelper.waitSimply(2);

		logger.info("Getting notification title name");
		String notificationTitle = webAppHelper.findElementByXPath("//*[contains(@class ,'dropdown-header')]")
				.getText();

		logger.info("Asserting notification title name");
		Assert.assertTrue(notificationTitle.contains("You have") && notificationTitle.contains("notifications"));

		countNotification = notificationTitle.split("You have")[1].split("notifications")[0].trim();

		int noticationNum = Integer.parseInt(countNotification);

		if (noticationNum > 0) {

			logger.info("Getting notification messages count");
			int messageCount = webAppHelper.findElementsByXPath("//*[local-name()='svg' and contains(@class,'mr-2 ')]")
					.size();

			logger.info("Asserting  notification message's count");
			Assert.assertEquals(messageCount, noticationNum);
		}

	}

	@Test(enabled = true, priority = 9)
	public void jobQueuedTest() {

		logger.info("Getting Run Report disabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		if (runReport.contains("setBgColor")) {

			logger.info("Check notification message text");
			String notificationMsge = webAppHelper.findElementByXPath("//div[contains(@class , 'dropdown-menu ')]")
					.getText();

			logger.info("Asserting notification message class name ");
			if (notificationMsge.contains("Queued")) {

				logger.info("Asserting Job : Queued notification message class name successfull");
			}

		}

	}

	@Test(enabled = true, priority = 10)
	public void downLoadReportNotification() {

		logger.info("Getting Run Report disabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		if (runReport.contains("setBgColor")) {

			logger.info("Check notification message Download the report text");
			String notificationMsge = webAppHelper.findElementByXPath("//div[contains(@class , 'dropdown-menu ')]")
					.getText();

			logger.info("Asserting notification message Download the report class name  ");
			if (notificationMsge.contains("Download the report")) {

				logger.info("Asserting  Download the report notification  class name successfull");

			}

		}

	}

	@Test(enabled = true, priority = 11)
	public void jobCompletedTest() {

		webAppHelper.waitForElementToBeLoaded("//*[text()='Run Report']//parent::div");
		logger.info("Getting Run Report disabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report disabled class name ");

		if (runReport.contains("setBgColor")) {

			logger.info("Getting Job : Completed notification message text");
			String notificationMsge = webAppHelper.findElementByXPath("//div[contains(@class , 'dropdown-menu ')]")
					.getText();

			logger.info("Asserting  Job : Completed ");
			if (notificationMsge.contains(" Job : Completed")) {

				logger.info("Asserting  Job : Completed successfull");
			}
		}
	}

	@Test(enabled = true, priority = 12)
	public void runReportEnableTest() {

		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("Getting Job : Last Job Completed notification message text");

			String notificationMsge = webAppHelper.findElementByXPath("//div[contains(@class , 'dropdown-menu ')]")
					.getText();
			if (notificationMsge.contains("Job : Last Job Completed")
					|| notificationMsge.contains("Job : Last Job Failed"))

				logger.info("Asserting  Run Report enable successfull");
			logger.info("Getting default value of Include OTS As of Date field option");
			String defaultValue = webAppHelper
					.findElementsByXPath(
							"//div[@class='react-datepicker-wrapper']//parent::div//parent::div//parent::div")
					.get(0).getAttribute("class");

			Assert.assertEquals(defaultValue, "form-group");

		} else {

			logger.info("Getting Run report blocker message");
			String runReportMsge = webAppHelper.findElementByXPath("//p[@class='text-center']").getText();

			logger.info("Asserting Run report Disabled Message");
			Assert.assertEquals(runReportMsge,
					"*You can run a new report once the previous job is completed and downloaded!");
		}

		logger.info("clicking notification icon");
		webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-item ')]").click();

	}

	@Test(enabled = true, priority = 13)
	public void otsCalculationLableNameTest() {

		logger.info("getting include stock header text");
		String includeStock = webAppHelper.findElementsByClassName("custom-control-label").get(0).getText();
		logger.info("Asserting Include Stock Text title name");
		Assert.assertEquals(includeStock, "Include Stock");

		logger.info("Getting Include WIP header text");
		String includeWIP = webAppHelper.findElementsByClassName("custom-control-label").get(1).getText();
		logger.info("Asserting Include WIP Text title name");
		Assert.assertEquals(includeWIP, "Include WIP(Based on In Warehouse Date)");

		logger.info("Getting Include Orders header text");
		String includeOrders = webAppHelper.findElementsByClassName("custom-control-label").get(2).getText();
		logger.info("Asserting Include Orders Text title name");
		Assert.assertEquals(includeOrders, "Include Orders(Based on Order Complete Date)");

		logger.info("Getting ingnore stock text");
		String ignoreStock = webAppHelper.findElementsByClassName("custom-control-label").get(3).getText();
		logger.info("Asserting Ingnore Stock Text title name");
		Assert.assertEquals(ignoreStock, "Ignore Stock When Below Zero");

		logger.info("Getting Consider Allocated Stock text");
		String allocatedStock = webAppHelper.findElementsByClassName("custom-control-label").get(4).getText();

		logger.info("Asserting Consider Allocated Stock Text title name");
		Assert.assertEquals(allocatedStock, "Consider Allocated Stock");

		logger.info("Getting Consider Allocated WIP text");
		String allocatedWIP = webAppHelper.findElementsByClassName("custom-control-label").get(5).getText();
		logger.info("Asserting Consider Allocated WIP text title name");
		Assert.assertEquals(allocatedWIP, "Consider Allocated WIP");

		logger.info("Getting Consider Pick Tickets text");
		String pickTickets = webAppHelper.findElementsByClassName("custom-control-label").get(6).getText();
		logger.info("Asserting Consider Pick Tickets title name");
		Assert.assertEquals(pickTickets, "Consider Pick Tickets");

	}

	@Test(enabled = true, priority = 14, dependsOnMethods = "otsCalculationLableNameTest")
	public void OTSCalculationCheckBoxClickTest() {

		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("Unselect Getting Include Stock checkbox");
			webAppHelper.findElementsByXPath("//label[contains(@for,'Include')]").get(0).click();

			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");

			logger.info("Asserting Run report class name");
			Assert.assertTrue(runReportAfter.contains("disabled"));

			logger.info("Clicking Consider Pick Tickets");
			webAppHelper.findElementByXPath("//label[contains(@for,'Tickets')]").click();

			logger.info("clicking Ignore Stock When Below Zero  checkbox");
			webAppHelper.findElementByXPath("//label[contains(@for,'Ignore')]").click();

			String runReportEn = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");

			logger.info("Asserting Run report class name");
			Assert.assertTrue(runReportEn.contains("enableDiv"));

			logger.info("Unselect Getting Include Stock checkbox");
			webAppHelper.findElementsByXPath("//label[contains(@for,'Include')]").get(0).click();

		} else {

			logger.info("clicking include WIP  checkbox");
			webAppHelper.findElementsByXPath("//label[contains(@for,'Include')]").get(1).click();

			logger.info("clicking Ignore Stock When Below Zero  checkbox");
			webAppHelper.findElementByXPath("//*[text()='Ignore Stock When Below Zero']").click();

			logger.info("clicking Consider Allocated WIP  checkbox");
			webAppHelper.findElementByXPath("//*[text()='Consider Allocated WIP']").click();

		}

	}

	@Test(enabled = true, priority = 15, dependsOnMethods = "OTSCalculationCheckBoxClickTest")
	public void OTSCalculationCheckBoxUncheckTest() {

		logger.info("Deselecting include WIP");
		webAppHelper.findElementsByXPath("//label[contains(@for,'Include')]").get(1).click();

		logger.info("Deselecting Consider Allocated WIP");
		webAppHelper.findElementByXPath("//*[text()='Consider Allocated WIP']").click();

	}

	@Test(enabled = true, priority = 16)
	public void OTSDateTestIncludeOTS() {

		logger.info("Getting Include OTS As of Date name");
		String otsDatetext = webAppHelper.findElementByXPath("//div[contains(@class,'col-7')]//child::span").getText();

		logger.info("Asserting Include OTS As of Date name");
		Assert.assertEquals(otsDatetext, "Include OTS As of Date");

		logger.info("check Include OTS As of Date Textbox");
		webAppHelper.findElementByXPath("//div[contains(@class,'col-5')]");

	}

	@Test(enabled = true, priority = 17)
	public void OTSDateTestIncludeOTSOptions() {

		logger.info("Clicking Include OTS As of Date dropdown icon");
		webAppHelper.findElementByXPath("//div[contains(@class,'col-5')]//child::select").click();

		logger.info("Getting dropdown icon options text");
		String noOption = webAppHelper
				.findElementsByXPath("//div[contains(@class,'col-5')]//child::select//child::option").get(0).getText();

		logger.info("Asserting No Option in Include OTS As of a Date ");
		Assert.assertEquals(noOption, "No");

		logger.info("Getting dropdown icon options text");
		String yesOption = webAppHelper
				.findElementsByXPath("//div[contains(@class,'col-5')]//child::select//child::option").get(1).getText();

		logger.info("Asserting Yes Option in Include OTS As of a Date ");
		Assert.assertEquals(yesOption, "Yes");

		logger.info("Closing Dropdown of Include OTS As of Date");
		webAppHelper.findElementByXPath("//div[contains(@class,'col-5')]//child::select").click();

	}

	@Test(enabled = true, priority = 18, dependsOnMethods = "OTSDateTestIncludeOTSOptions")
	public void OTSDateTestDefaultOption() {

		logger.info("Getting default value of Include OTS As of Date field option");
		String defaultValue = webAppHelper
				.findElementByXPath("//div[contains(@class,'col-5')]//child::select//child::option").getText();

		Assert.assertEquals(defaultValue, "No");

		logger.info("Getting css of Include OTS As of Date field Default option ");
		String otsDateDefaultcss = webAppHelper.findElementsByXPath(
				"//input[@class='form-control']//parent::div//parent::div//parent::div//parent::div//parent::div")
				.get(0).getAttribute("class");

		logger.info("Asserting css of Include OTS As of Date field Default Option");
		Assert.assertEquals(otsDateDefaultcss, "form-group");

	}

	@Test(enabled = true, priority = 19, dependsOnMethods = "OTSDateTestDefaultOption")
	public void OTSDateTestYesOption() {

		logger.info("Clicking Include OTS As od Date dropdown icon");
		webAppHelper.findElementByXPath("//select[@class='form-control']").click();

		logger.info("clicking Yes option");
		webAppHelper.waitSimply(1);
		webAppHelper.findElementByXPath("//option[text()='Yes']").click();

		logger.info("Clicking Include OTS As od Date dropdown icon");
		webAppHelper.findElementByXPath("//select[@class='form-control']").click();

	}

	@Test(enabled = true, priority = 20, dependsOnMethods = "OTSDateTestYesOption")
	public void OTSDateTestYesOptionCSS() {

		logger.info("Getting css of Include OTS As of Date field Yes option ");
		String otsDateAftercss = webAppHelper.findElementsByXPath(
				"//input[@class='form-control']//parent::div//parent::div//parent::div//parent::div//parent::div")
				.get(0).getAttribute("class");

		logger.info("Asserting css of Include OTS As of Date field yes Option");
		Assert.assertEquals(otsDateAftercss, "form-group mt-3");

		logger.info("Getting css of Date field and add symbol icon");
		String otsDatecssID = webAppHelper.findElementsByXPath(
				"//input[@class='form-control']//parent::div//parent::div//parent::div//parent::div//parent::div")
				.get(5).getAttribute("class");

		logger.info("Asserting css class name of Date field and add symbol icon");
		Assert.assertEquals(otsDatecssID, "col-3 col-md-3");

	}

	@Test(enabled = true, priority = 21, dependsOnMethods = "OTSDateTestYesOptionCSS")
	public void dateFieldAndADDIconTest() {

		logger.info("Getting Date field text box");
		webAppHelper.findElementByClassName("react-datepicker-wrapper");

		logger.info("Getting Add field symbol icon");
		String svgIcon = webAppHelper.findElementByXPath("//div[contains(@class,'col-3')]//*[local-name()='svg']")
				.getAttribute("data-icon");

		Assert.assertEquals(svgIcon, "plus");

		logger.info("clicking Yes option");
		webAppHelper.findElementByXPath("//option[@value='Yes']").click();

		logger.info("Getting Date field format");
		String dateformat = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input")
				.getAttribute("placeholder");

		logger.info("Asserting Date format");
		Assert.assertEquals(dateformat, "MM/DD/YYYY");
	}

	@Test(enabled = true, priority = 22, dependsOnMethods = "dateFieldAndADDIconTest")
	public void otsDateDatePickerTest() {

		logger.info("Getting Date field class before Click");
		String dateFieldClassBefore = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input")
				.getAttribute("class");

		logger.info("Asserting Date field class name before click");
		Assert.assertEquals(dateFieldClassBefore, "form-control");

		logger.info("Clicking Date field text box");
		webAppHelper.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input").click();

		logger.info("Getting Date field class before Click");
		String dateFieldClassAfter = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input")
				.getAttribute("class");

		logger.info("Asserting Date field class name before click");
		Assert.assertTrue(dateFieldClassAfter.contains(" react-datepicker-ignore-onclickoutside"));

	}

	@Test(enabled = true, priority = 23, dependsOnMethods = "otsDateDatePickerTest")
	public void datePickerHeaderTest() {

		LocalDate currentdate = LocalDate.now();

		Month currentMonth = currentdate.getMonth();

		int currentYear = currentdate.getYear();

		logger.info("Getting Date Picker Current month and Year");
		String currentDate = webAppHelper.findElementByClassName("react-datepicker__current-month").getText();

		logger.info("Checking current and Year in Date picker");
		currentDate.equalsIgnoreCase(currentMonth + " " + currentYear);

		logger.info("Getting text of Date picker arrow icon");
		String arrowIconText = webAppHelper
				.findElementByXPath("//span[contains(@class , ' react-datepicker__navigation-icon--next')]").getText();

		logger.info("Asserting Arrow Icon of Next month Text");
		Assert.assertEquals(arrowIconText, "Next Month");

	}

	@Test(enabled = true, priority = 24, dependsOnMethods = "datePickerHeaderTest")
	public void datePickerDayNameTest() {

		logger.info("Getting Date Picker Current month and Year");
		int currentDate = webAppHelper.findElementsByXPath("//div[@class='react-datepicker__day-names']//child::div")
				.size();

		logger.info("Asserting Day names tags");
		Assert.assertEquals(currentDate, 7);

	}

	@Test(enabled = true, priority = 25, dependsOnMethods = "datePickerDayNameTest")
	public void datePickePreviousrArrowIconClickTest() {

		logger.info("Clicking arrow icon next month in Date picker");
		webAppHelper.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--next')]").click();

		logger.info("Getting text of Date picker arrow icon");
		String arrowIconText = webAppHelper
				.findElementByXPath("//span[contains(@class , ' react-datepicker__navigation-icon--previous')]")
				.getText();

		logger.info("Asserting Arrow Icon of Next month Text");
		Assert.assertEquals(arrowIconText, "Previous Month");

		logger.info("Clicking Arrow icon in Date Picker");
		webAppHelper.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--previous')]")
				.click();

		LocalDate currentdate = LocalDate.now();

		// Getting current month name
		Month currentMonth = currentdate.getMonth();

		// Getting current year
		int currentYear = currentdate.getYear();

		logger.info("Getting Date Picker Current month and Year");
		String currentDate = webAppHelper.findElementByClassName("react-datepicker__current-month").getText();

		logger.info("Checking current month and Year in Date picker");
		currentDate.equalsIgnoreCase(currentMonth + " " + currentYear);

	}

	@Test(enabled = true, priority = 26, dependsOnMethods = "datePickePreviousrArrowIconClickTest")
	public void datePickerGrayedAndActiveDayTest() {

		logger.info("Getting Current day class name");
		String currentDay = webAppHelper.findElementsByXPath("//div[text()=" + dayOfMonth + "]").get(0)
				.getAttribute("class");

		logger.info("Asserting current Day of Month class name");
		Assert.assertTrue(currentDay.contains(" react-datepicker__day--today"));
		int currentDate = dayOfMonth + 1;

		logger.info("Getting next Day of Month class name");
		String nextDayClass = webAppHelper.findElementByXPath("//div[text()=" + currentDate + "]")
				.getAttribute("class");

		logger.info("Asserting next Day of Month class name");
		Assert.assertTrue(nextDayClass.contains("react-datepicker__day"));
	}

	@Test(enabled = true, priority = 27, dependsOnMethods = "datePickerGrayedAndActiveDayTest")
	public void currentDateTest() {

		logger.info("Getting current Date class name ");
		String currentDayClass = webAppHelper
				.findElementByXPath("//div[contains(@class , ' react-datepicker__day--today')]").getText();

		logger.info("Asserting current day and showing current day class");
		Assert.assertTrue(currentDayClass.contains("" + dayOfMonth));

		webAppHelper.findElementByXPath("//div[contains(@class , ' react-datepicker__day--today')]").click();

		logger.info("Getting date calender field box before adding");
		int dateCalenderBefore = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Adding Date field box");
		webAppHelper.findElementByXPath("//*[local-name()='svg' and (@data-icon='plus')]").click();

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count  Date Calender count");
		Assert.assertTrue(dateCalenderBefore < dateCalenderAfter);

		logger.info("Checking date calender fieldbox by Clicking Add symbol icon ");
		webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

	}

	@Test(enabled = true, priority = 28, dependsOnMethods = "datePickerGrayedAndActiveDayTest")
	public void datePickerSelectDateTest() {

		logger.info("Clicking Date field text box");
		webAppHelper.findElementsByXPath("//div[@class='react-datepicker__input-container']//child::input").get(1)
				.click();

		logger.info("Clicking arrow icon next month in Date picker");
		webAppHelper.findElementByXPath("//button[contains(@class , ' react-datepicker__navigation--next')]").click();

		logger.info("Getting A Day of Month class name");
		webAppHelper.findElementByXPath("//*[text() = '10' ]").click();
		webAppHelper.waitSimply(4);

		logger.info("Adding Date field box");
		webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='plus')]").get(1).click();

	}

	@Test(enabled = true, priority = 29, dependsOnMethods = "datePickerGrayedAndActiveDayTest")
	public void datePickerHeaderdatefield2() {

		logger.info("Clicking Date field text box");
		webAppHelper.findElementsByXPath("//div[@class='react-datepicker__input-container']//child::input").get(2)
				.click();

		logger.info("Getting header class name for Calender window");
		String CalHeader = webAppHelper
				.findElementsByXPath("//div[contains(@class,'react-datepicker__header')]//child::div").get(0)
				.getAttribute("class");

		logger.info("Asserting Header name class text");
		Assert.assertEquals(CalHeader, "react-datepicker__current-month");
	}

	@Test(enabled = true, priority = 30, dependsOnMethods = "datePickerHeaderdatefield2")
	public void datePickerSendDatefield2() {

		logger.info("Getting date field component");
		WebElement dateBox1 = webAppHelper
				.findElementByXPath("//input[contains(@class , ' react-datepicker-ignore-onclickoutside')]");

		logger.info("Asserting a date in date field textbox");
		dateBox1.sendKeys("08/10/2024");

		logger.info("Adding date calender fieldbox");
		webAppHelper.findElementsByXPath("  //*[local-name()='svg' and (@data-icon='plus')]").get(2).click();

	}

	@Test(enabled = true, priority = 31, dependsOnMethods = "datePickerSendDatefield2")
	public void OTSDateTestAddTest() {

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Getting Add symbol icon ");
		int addSymbol = webAppHelper.findElementsByXPath("  //*[local-name()='svg' and (@data-icon='plus')]").size();

		Assert.assertEquals(dateCalenderAfter, addSymbol);

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderField = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Getting Subtraction  symbol icon ");
		int subSymbol = webAppHelper.findElementsByXPath("  //*[local-name()='svg' and (@data-icon='minus')]").size();

		logger.info("Asserting subtraction symbols icon's present");
		Assert.assertEquals(dateCalenderField, subSymbol);
	}

	@Test(enabled = true, priority = 32, dependsOnMethods = "OTSDateTestAddTest")
	public void OTSDateTestRemoveDatefield() {

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Removing date calender field box");
		webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='minus')]").get(2).click();

		logger.info("Getting date calender fieldbox after removing");
		int dateCalenderRemove = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Asserting of count  Date Calender count");
		Assert.assertTrue(dateCalenderAfter > dateCalenderRemove);

	}

	@Test(enabled = true, priority = 33, dependsOnMethods = "OTSDateTestRemoveDatefield")
	public void afterRemovedDatefieldAddSymbol() {

		logger.info("Getting date calender fieldbox count after adding");
		int dateCalenderAfter = webAppHelper.findElementsByClassName("react-datepicker__input-container").size();

		logger.info("Getting Add symbol icon ");
		int symbols = webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='minus')]").size();

		Assert.assertEquals(dateCalenderAfter, symbols);
	}

	@Test(enabled = true, priority = 34, dependsOnMethods = "OTSDateTestRemoveDatefield")
	public void afterRemovedSpecificDatefield() {

		logger.info("Removing date calender field box");
		webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='minus')]").get(1).click();

		logger.info("Getting Date field Value Text");
		String dateFieldValue = webAppHelper
				.findElementByXPath("//div[@class='react-datepicker__input-container']//child::input")
				.getAttribute("value");

		String todayDate = webAppHelper.getToday("M/dd/yyyy");

		logger.info("Asserting specific remain value in Date field");
		Assert.assertEquals(dateFieldValue, todayDate);

		logger.info("Removing Empty Date field");
		webAppHelper.findElementsByXPath("//*[local-name()='svg' and (@data-icon='minus')]").get(1).click();

	}

	@Test(enabled = true, priority = 35)
	public void wareHouseTestAllWarehouseText() {

		webAppHelper.scrollPageDown();

		webAppHelper.waitSimply(2);

		logger.info("Getting All Warehouses Combined text");
		String allWarehouseText = webAppHelper.findElementByXPath("//label[contains(@for,'All ')]").getText();

		logger.info("Asserting All Warehouses Combined Text ");
		Assert.assertEquals(allWarehouseText, "All Warehouses Combined");

	}

	@Test(enabled = true, priority = 36)
	public void wareHouseDisplayStockTest() {

		logger.info("Getting Display Stock by Specified Warehouse:");
		String displaystockText = webAppHelper.findElementByXPath("//label[contains(@for,'Display')]").getText();

		logger.info("Asserting Display Stock by Specified Warehouse: Text ");
		Assert.assertEquals(displaystockText, "Display Stock by Specified Warehouse");

		logger.info("Clicking Display Stock");
		webAppHelper.findElementByXPath("//*[text()='Display Stock by Specified Warehouse']").click();
	}

	@Test(enabled = true, priority = 37, dependsOnMethods = "wareHouseDisplayStockTest")
	public void runReportDisplayStockTest() {

		logger.info("Getting Run Report Disabled class name");
		String runReportBefore = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Asserting Run report class name");
		Assert.assertTrue(runReportBefore.contains("text-center"));

		logger.info("Selecting Warehouse checkbox");
		webAppHelper.findElementsByXPath("//div[@id='display_warehouse']//child::label").get(1).click();

		logger.info("Getting Run Report Enabled class name");
		String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Asserting Run report class name");
		if (runReportAfter.contains("enableDiv")) {
			logger.info("runReportDisplayStockTest completed");
		}

	}

	@Test(enabled = true, priority = 38, dependsOnMethods = "runReportDisplayStockTest")
	public void wareHouseTest() {

		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Checking Run report class name");
		if (runReport.contains("enableDiv")) {

			logger.info("clicking Display Stock by Specified Warehouse radio button");
			webAppHelper.findElementByXPath("//*[text()='All Warehouses Combined']").click();

			logger.info("Getting Run Report class name");
			String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
					.getAttribute("class");
			logger.info("Asserting Run report class name");
			Assert.assertTrue(runReportAfter.contains("enableDiv"));

		}

	}

	@Test(enabled = true, priority = 39, dependsOnMethods = "wareHouseTest")
	public void diplayStockCheckBoxClickingTest() {

		logger.info("clicking Display Stock by Specified Warehouse radio button");
		webAppHelper.findElementByXPath("//*[text()='Display Stock by Specified Warehouse']").click();

		int warehousecheckbox = webAppHelper.findElementsByXPath("//div[@id='display_warehouse']//child::label").size();

		for (int i = 0; i < warehousecheckbox; i++) {

			if (i == 0 || i == 1 || i == 2 || i == 4 || i == 6) {
				logger.info("clicking selected warehouse checkbox");
				webAppHelper.findElementsByXPath("//div[@id='display_warehouse']//child::label").get(i).click();
			}
		}

	}

	@Test(enabled = true, priority = 40)
	public void tagAllAndUnTagTest() {

		logger.info("Check tag all button");
		String tagText = webAppHelper.findElementById("check").getText();

		logger.info("Asserting Tag All Text");
		Assert.assertEquals(tagText, "Tag All");

		logger.info("Clicking Tag All button");
		webAppHelper.findElementByXPath("//*[text()='Tag All']").click();

		logger.info("Getting Un tag all button text");
		String unTagText = webAppHelper.findElementById("uncheck").getText();

		logger.info("Asserting and clicking Un tag All Text");
		Assert.assertEquals(unTagText, "Untag All");
		webAppHelper.findElementByXPath("//*[text()='Untag All']").click();

	}

	@Test(enabled = true, priority = 41)
	public void diplayStockTagAllTest() {

		logger.info("cliking tag all button");
		webAppHelper.findElementById("check").click();

		logger.info("Getting Run Report Enabled class name");
		String runReportAfter = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");
		logger.info("Asserting Run report class name");
		if (runReportAfter.contains("enableDiv")) {

			logger.info("cliking untag all button");
			webAppHelper.findElementById("uncheck").click();
			webAppHelper.waitSimply(2);

			logger.info("Getting Run Report Disabled class name");
			String runReportBefore = webAppHelper.findElementByXPath("//*[text()='Run Report']").getAttribute("class");
			logger.info("Asserting Run report class name");
			Assert.assertTrue(runReportBefore.contains("disabled"));

		} else {

			logger.info("cliking untag all button");
			webAppHelper.findElementById("uncheck").click();

		}

	}

	@Test(enabled = true, priority = 42, dependsOnMethods = "diplayStockTagAllTest")
	public void warehousesTest() {

		logger.info(" Getting warehouse checkbox");
		int wareHouseCheckbox = webAppHelper.findElementsByXPath("//label[contains(@class,'m-0')]").size();

		for (int i = 0; i < wareHouseCheckbox; i++) {

			if (i == 1 || i == 2 || i == 4) {
				logger.info("selecting  warehouse checkbox");
				webAppHelper.findElementsByXPath("//label[contains(@class,'m-0')]").get(i).click();
			}
		}

	}

	@Test(enabled = true, priority = 43)
	public void otsBySizeText() {

		webAppHelper.scrollPageDown();

		webAppHelper.waitSimply(2);

		logger.info("getting Display ots By Size text");
		String otsBySize = webAppHelper.findElementByXPath("//label[contains(@class, ' font-weight-bold')]").getText();

		logger.info("Asserting Display ots By Size Text title name");
		Assert.assertEquals(otsBySize, "Display OTS by Size?");

	}

	@Test(enabled = true, priority = 44, dependsOnMethods = "otsBySizeText")
	public void otsBySizeTest() {

		logger.info("clicking Display ots By Size checkbox");
		webAppHelper.waitSimply(1);
		webAppHelper.findElementById("otsBySize").click();

		logger.info("DeSelecting Display ots By Size checkbox");
		webAppHelper.waitSimply(1);
		webAppHelper.findElementByXPath("//*[text()='Display OTS by Size?']").click();

	}

	@Test(enabled = true, priority = 45)
	public void footerRightSideText() {

		logger.info("getting Copyright © 2022 AIMS360 text");
		String copyrightText = webAppHelper.findElementByClassName("ml-1").getText();

		logger.info("Asserting Copyright © 2022 AIMS360 text name");
		Assert.assertEquals(copyrightText, "© 2022 AIMS360");

	}

	@Test(enabled = false, priority = 46)
	public void footerLeftSideText() {

		logger.info("getting left side text");
		String versionName = webAppHelper.findElementByXPath("//span[@role='button']").getText();

		logger.info("clicking and Asserting version text");
		Assert.assertEquals(versionName, "V2022.11.10");
		webAppHelper.findElementByXPath("//span[@role='button']").click();

	}

	@Test(enabled = false, priority = 47)
	public void runReportTest() {

		logger.info("Getting Run Report Enabled class name");
		String runReport = webAppHelper.findElementByXPath("//*[text()='Run Report']//parent::div")
				.getAttribute("class");

		logger.info("Asserting Run Report Enabled class name ");
		if (runReport.contains("enableDiv")) {

			logger.info("Clicking Run Report button");
			webAppHelper.findElementByXPath("//*[text()='Run Report']").click();
			webAppHelper.waitSimply(3);

			logger.info("Getting Alert window class name");
			String alertClass = webAppHelper.findElementsByXPath("//div[contains(@class , 'modal-dialog')]//child::div")
					.get(0).getAttribute("class");

			logger.info("Asserting Alert window class name");
			Assert.assertEquals(alertClass, "modal-content");

			logger.info("Getting header title name for alert window");
			String alertTitleName = webAppHelper.findElementByClassName("modal-title").getText();

			logger.info("Asserting title name for Alert window");
			Assert.assertEquals(alertTitleName, "Alert");

			logger.info("Getting Alert message");
			String alertMsge = webAppHelper.findElementByClassName("modal-body").getText();

			logger.info("Asserting title name for Alert window");
			Assert.assertEquals(alertMsge, "Jobs triggered successfully");

			logger.info("Closing Alert window");
			webAppHelper.findElementByXPath("//*[text()='Close']").click();

			logger.info("Getting Run report disabled help message");
			String runReportMsge = webAppHelper.findElementByXPath("//p[@class='text-center']").getText();

			logger.info("Asserting Run report Disabled Message");
			Assert.assertEquals(runReportMsge,
					"*You can run a new report once the previous job is completed and downloaded!");
			webAppHelper.scrollPageUp();

			for (int i = 0; i < 40; ++i) {

				logger.info("Clicking notification icon");
				webAppHelper.findElementByXPath("//*[contains(@class , 'c-header-nav-item ')]").click();

				logger.info("Check notification message text");
				String notificationMsge = webAppHelper.findElementsByXPath(" //div[contains(@class , 'dropdown-menu')]")
						.get(0).getText();

				if (notificationMsge.contains("Download the report")) {

					logger.info("Clicking Download the report");
					webAppHelper.findElementByXPath("//*[text()=' Download the report']").click();
					webAppHelper.waitSimply(5);

					logger.info("Getting Alert window title");
					String alertTitle = webAppHelper
							.findElementByXPath("//div[contains(@class , 'modal-dialog')]//child::h5").getText();

					logger.info("Asserting Alert title name");
					Assert.assertEquals(alertTitle, "Alert");

					logger.info("Getting Alert message after downloading report");
					String alertMsgeAfterClick = webAppHelper
							.findElementByXPath("//div[contains(@class , 'modal-body')]").getText();

					logger.info("Getting Alert message");
					Assert.assertEquals(alertMsgeAfterClick, "File download successfully");

					logger.info("Closing alert window");
					webAppHelper.findElementByXPath("//*[text() ='Close']").click();

					break;

				}

				else {
					webAppHelper.refreshBrowser();
					webAppHelper.waitSimply(30);

				}

			}
		}
	}

	@Test(enabled = false, priority = 48, dependsOnMethods = "runReportTest")
	public void fileTest() {
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader("C:\\Users\\NEXGEN\\Downloads\\OTS_AS_OF_Date_Report (1).csv"));

			// this will load content into list
			List<String[]> li = reader.readAll();
			System.out.println("Total rows which we have is " + li.size());

			// create Iterator reference
			Iterator<String[]> i1 = li.iterator();

			// Iterate all values
			while (i1.hasNext()) {

				String[] str = i1.next();

				System.out.print(" Values are ");

				for (int i = 0; i < str.length; i++) {

					System.out.print(" " + str[i]);

				}
				break;
			}
		} catch (Exception e) {
			System.out.print("File Not found exception");
			e.printStackTrace();
		}

	}

	@Test(enabled = true, priority = 49)
	public void logOut() {

		webAppHelper.scrollPageUp();
		logger.info("Selecting profile icon");
		webAppHelper.findElementByClassName("c-avatar").click();

		logger.info("clicking Logout option");
		webAppHelper.findElementByXPath("//a[text()='Logout']").click();

		webAppHelper.waitForElementToBeLoadedByTagName("h5");

		logger.info("Getting login text");
		String loginTitleName = webAppHelper.findElementByTagName("h5").getText();

		logger.info("Asserting Login");
		Assert.assertEquals(loginTitleName, "Runway Login");

	}

	@AfterClass
	public void unsetup() {
		webAppHelper.quitBrowser();
	}

}
