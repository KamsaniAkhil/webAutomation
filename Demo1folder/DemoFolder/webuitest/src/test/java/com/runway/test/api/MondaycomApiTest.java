package com.runway.test.api;

import static io.restassured.RestAssured.expect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.runway.test.ui.common.utils.APITestHelper;
import com.runway.test.ui.common.utils.UITestSeleniumHelper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MondaycomApiTest implements IRetryAnalyzer, IAnnotationTransformer {
	static Logger logger = LoggerFactory.getLogger(OTSApiTest.class);

	public String xfunctionKey = System.getProperty("x-functions-key", APITestHelper.x_functions_key);
	public static String bearerToken = System.getProperty("bearer",
			"eyJhbGciOiJIUzI1NiJ9.eyJ0aWQiOjE3Njc0MzA0NCwidWlkIjozMzMwMzU4MSwiaWFkIjoiMjAyMi0wOC0yMlQxMToxOToxNC44ODZaIiwicGVyIjoibWU6d3JpdGUiLCJhY3RpZCI6ODAxOTYzOCwicmduIjoidXNlMSJ9.4s1PdcIU209INFRG1NnZgzr2QhmFXWdKIcWmCb4YNdg");
	public static String settingsToken = System.getProperty("settingsToken",
			"eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIyZDY1Y2VjNS1lYWIwLTRiZDUtOTU0Yy04OWU3ODU1MjMwNWYiLCJuYW1lIjoiQW50aG9ueSBQYXNzZWxhdCIsIkVtYWlsIjoiYW50aG9ueSt3aXRyZUBhaW1zMzYwLmNvbSIsImlzcyI6Imh0dHBzOi8vYXBpLmFpbXMzNjAucmVzdC9hdXRoZW50aWNhdGlvbi92MS9hdXRoZW50aWNhdGlvbi90b2tlbiIsImlhdCI6IjIwMjItMTEtMTVUMjA6MDE6MzAuMzA4ODI4KzAwOjAwIiwiZWF0IjoiMjEyMS0xMS0xNVQyMDowMTozMC4zMDg4MjgrMDA6MDAifQ.XmSh1hXrOS7jg2Uobv-NoIY0eNOha1CCq1C-smHjGL6iBBCez4J255j9f7y50aLgvALvPKsKsvWKXbOJ_dna_Q");
	int retryCount = 0;
	int maxRetryCount = 2;
	private String version = null;
	APITestHelper apiHelper = new APITestHelper();
	UITestSeleniumHelper webAppHelper = new UITestSeleniumHelper();

	public boolean retry(ITestResult result) {
		if (retryCount < maxRetryCount) {
			System.out.println("Retrying test " + result.getName() + " with status "
					+ getResultStatusName(result.getStatus()) + " for the " + (retryCount + 1) + " time(s).");
			retryCount++;
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
		return false;
	}

	public void transform(ITestAnnotation testannotation, Class testClass, Constructor testConstructor,
			Method testMethod) {
		IRetryAnalyzer retry = testannotation.getRetryAnalyzer();

		if (retry == null) {
			testannotation.setRetryAnalyzer(OTSApiTest.class);
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
	public void GetPODetailsTest() {

		RestAssured.baseURI = apiHelper.API_BaseURL;

		Response res = expect().statusCode(Integer.parseInt("200")).given().contentType("application/json")
				.header("Authorization", "Bearer " + settingsToken)
				.queryParam("$filter",
						"type eq 'Style' and lastModifiedOn ge  1899-12-30T07:00:39-07:00 and  lastModifiedOn le 2022-04-25T14:28:38-07:00")
				.queryParam("$expand", "stylePOLines").queryParam("$count", false).queryParam("pagesize", 250).when()
				.log().everything().get(apiHelper.API_POPATH);
		res.then().log().all();

	}

	@Test(enabled = false, priority = 2)
	public void CustomerDetailsTest() {
		RestAssured.baseURI = apiHelper.API_BaseURL;
		Response res = (Response) expect().statusCode(Integer.parseInt("200")).given().contentType("application/json")
				.header("Authorization", "Bearer " + settingsToken)
				.queryParam("$filter", "lineItems/any(s:s/allocations/any(b:b/wipReference eq '50468'))")
				.queryParam("$expand", "lineitems($expand=allocations)").queryParam("$count", "false").when().log()
				.everything().get(apiHelper.RUNWAY_CUSTOMERDETAILS_PATH);
		res.then().log().all();

	}

	@Test(enabled = false, priority = 3)
	public void toGetJobs() {

		RestAssured.baseURI = "https://runwayjobmanager-w.aims360runway.com/";
		JsonObject reqObject = new JsonObject();

		reqObject.addProperty("getBy", "moduleType");
		reqObject.addProperty("getByValue", "MondayCom");
		Response res = expect().statusCode(Integer.parseInt("200")).given().contentType("application/json")
				.header("Authorization", "Bearer " + settingsToken).body(reqObject.toString()).log().everything().when()
				.post(apiHelper.RUNWAY_JobManager_Path);
		res.then().log().all();

	}

	@Test(enabled = true, priority = 4)
	public void GetSyncDataDetails() {

		RestAssured.baseURI = "https://runwaymondaycomsync-w.aims360runway.com";

		JsonObject reqObject = new JsonObject();

		reqObject.addProperty("requestType", "syncData");
		reqObject.addProperty("jobId", "FD396240-F124-4184-AEEE-78D445A17A31");

		Response res = expect().statusCode(Integer.parseInt("200")).given()
				.header("Authorization", "Bearer " + settingsToken).body(reqObject.toString()).log().everything().when()
				.post(apiHelper.RUNWAY_MondayCom_Path);
		res.then().log().all();
	}

	@Test(enabled = false, priority = 5)
	public void GetSettingsService() {

		RestAssured.baseURI = apiHelper.RUNWAY_SettingsService_URI;
		JsonObject reqObject = new JsonObject();
		reqObject.addProperty("requestType", "initial");
		reqObject.addProperty("moduleType", "Monday.com");
		reqObject.addProperty("mondaycomSettigns", "{}");

		Response res = expect().statusCode(Integer.parseInt("200")).given()
				.header("Authorization", "Bearer ", settingsToken).body(reqObject).log().everything().when()
				.post(apiHelper.RUNWAY_SettingsService_Path);
		res.then().log().all();
	}

	@Test(enabled = true, priority = 6)
	public void GetBoardsDetails() {
		RestAssured.baseURI = "https://api.monday.com";
		version = "/v2";

		JsonObject reqObject = new JsonObject();
		reqObject.addProperty("query",
				"query { boards () {id name state board_folder_id owner { id } groups { id title } }}");

		Response res = expect().statusCode(Integer.parseInt("200")).given()
				.header("Authorization", "Bearer " + bearerToken).contentType("application/json").body(reqObject).log()
				.everything().when().post(version);
		res.then().log().all();
	}

	@Test(enabled = false, priority = 7)
	public void PurchaseOrderByNumber() {

		RestAssured.baseURI = "https://apiwest.aims360.rest";
		Response res = expect().statusCode(Integer.parseInt("200")).given().header("Authorization", "Bearer "
				+ "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJlYTBlZTcwOS03MjIzLTRmOTUtYjc4YS01NWZiZDg3OGY0ZWQiLCJuYW1lIjoiQW50aG9ueSBQYXNzZWxhdCIsIkVtYWlsIjoiYW50aG9ueUBhaW1zMzYwLmNvbSIsImlzcyI6Imh0dHBzOi8vYXBpLmFpbXMzNjAucmVzdC9hdXRoZW50aWNhdGlvbi92MS9hdXRoZW50aWNhdGlvbi90b2tlbiIsImlhdCI6IjIwMjItMDUtMTlUMjI6MTc6MTYuMzkwMDk5NiswMDowMCIsImVhdCI6IjIxMjEtMDUtMTlUMjI6MTc6MTYuMzkwMDk5NiswMDowMCJ9.-eZUNz2AFb2xtpWl7xlZXD0bsPN4s44vFif9L2vlgyuD6e37w1jDQMHm70-BHZ5JHd-h8l6-2pna9P9SwHpDvw")
				.log().everything().when().get(apiHelper.RUNWAY_PurchaseOrder_Path);
		res.then().log().all();

	}

	@Test(enabled = false, priority = 8)
	public void GetStyleById() {

		RestAssured.baseURI = apiHelper.API_BaseURL;
		String styleId = "7af9b691-0a16-ec11-ae72-0050f2656f0f";

		Response res = expect().statusCode(Integer.parseInt("200")).given()
				.header("Authorization", "Bearer " + settingsToken).log().everything().when()
				.get(apiHelper.RUNWAY_STYLE_PATH + styleId);
		res.then().log().all();
	}

}
