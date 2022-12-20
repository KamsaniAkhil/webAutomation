package com.runway.test.api;

import static io.restassured.RestAssured.expect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class OTSApiTest implements IRetryAnalyzer, IAnnotationTransformer {
	static Logger logger = LoggerFactory.getLogger(OTSApiTest.class);

	public static String username = System.getProperty("email", "v-arungopi@aims360.com");
	public static String password = System.getProperty("password", "JR365@ims22!@");
	public static String redirectUrl = System.getProperty("redirectURL",
			"https://preprod-jr365.aims360.com/");
	public String xfunctionKey = System.getProperty("x-functions-key",APITestHelper.x_functions_key);
			
	String moduleType = null;
	 String clientKey = null;
	 String clientCode = null;
	 String callBackCode = null;
	 String accessToken = null;
     String securityCode = null;
     String zone = null;
	 int retryCount = 0;
	 int maxRetryCount = 2;

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
			return true;
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
    
	/* TestCaseId - C19029 */
	@Test(enabled = true, priority = 1)
	public void authenticate() {

		JsonObject reqObject = new JsonObject();
		reqObject.addProperty("clientKey", "CREAI");
		reqObject.addProperty("grant_type", "client_code");
		Response res = apiHelper.doLogin(reqObject);

		clientCode = apiHelper.getStringValue(res, "clientCode");

	}
	
	/* TestCaseId - C19033 */
	@Test(enabled = true, priority = 2)
	public void securityCodeAndZone() {
		
		JsonObject reqObject = new JsonObject(); 

		reqObject.addProperty("userName", "v-arungopi@aims360.com");

		reqObject.addProperty("password", "Creai@ims22!@");
		
		reqObject.addProperty("clientKey", "CREAI");
		reqObject.addProperty("grant_type", "password");
		Response res = apiHelper.doLogin(reqObject);
		
		securityCode = apiHelper.getStringValue(res, "securityCode");
		zone = apiHelper.getStringValue(res, "zone");
		
		System.out.println(securityCode+"\n"+zone);
		
	}

	@Test(enabled = false, priority = 3, dependsOnMethods = "securityCodeAndZone")
	public void login() {

		webAppHelper.openBrowser("chrome");
		webAppHelper.maximizeWindow();

		webAppHelper
				.navigateTo("https://prerod-runway.myrunway.fashion/#/clientcode?securitycode="+securityCode+"&zone="+zone+"&loginURL="+redirectUrl+"&addtionalroutes=/\r\n");
     
	}         
   
	/* TestCaseId - C19034 */
	@Test(enabled = true, priority = 4)//, dependsOnMethods = "login")
	public  void getAccessTokenApi() {

		JsonObject reqObject = new JsonObject();

		reqObject.addProperty("securityCode", securityCode);

		reqObject.addProperty("clientKey", "JR365");
		
		reqObject.addProperty("grant_type", "security_code");
		reqObject.addProperty("zone", zone);
		Response res = apiHelper.doLogin(reqObject);
	
//			 res = expect().statusCode(200).given()
//				.header("x-functions-key", xfunctionKey).body(reqObject.toString()).log().everything().when()
//        		.post(APITestHelper.API_LOGIN_PATH);
//		res.then().log().all();

		String json = res.asString();
		JsonPath jsonPath = new JsonPath(json);

//		boolean maintanancePage = jsonPath.getBoolean("isUnderMaintenance");
//		Assert.assertEquals(maintanancePage, false);
		
		HashMap tokentDetails = jsonPath.get("tokenInformaiton");
		//System.out.println("isUnderMaintenance :" + maintanancePage);

		accessToken = tokentDetails.get("accessToken").toString();
																																																																																																																																																																																																																																																					
		System.out.println("accessToken :" + accessToken);

	}
     
	/* TestCaseId - C19037 */
	@Test(enabled = true, priority = 5)//, dependsOnMethods = "getAccessTokenApi")
	public void getWarehouse() {
		
		RestAssured.baseURI = "https://dev-runwaywarehouses-e.aims360runway.com";
		
		 		 Response res = expect().statusCode(Integer.parseInt("200")).given()
			.contentType("application/json")
			.header("Authorization", "Bearer " + accessToken)
			.log().everything()
			 
		 .when().get(APITestHelper.API_WAREHOUSES_PATH);
		 res.then().log().all();
//		Response response = expect().statusCode(Integer.parseInt("200")).given()
//				.header("Authorization", "Bearer " + accessToken).contentType("application/json").log().everything()
//				.when().get(APITestHelper.API_WAREHOUSES_PATH);
//
//		response.then().log().all();

	}
	
	/* TestCaseId - C19035 */
	@Test(enabled = true, priority = 6)
	public void jobManagerCreateJob() {
		
		RestAssured.baseURI = "https://dev-runwayjobmanager-e.aims360runway.com";
		
		JsonObject reqObject = new JsonObject();
		 reqObject.addProperty("jobType", "Timmer");
		 reqObject.addProperty("moduleType", "OTS");
		 reqObject.addProperty("extension", ".xlsx");
		 reqObject.addProperty("param", "{}");
		 
		 Response res = expect().statusCode(Integer.parseInt("200")).given()
					.contentType("application/json")
					.header("Authorization", "Bearer " + accessToken)
					.header("x-functions-key", xfunctionKey)
					.body(reqObject.toString())
					.log().everything()
					 .when().get(APITestHelper.API_CREATEJOBMANAGER_PATH);
		 res.then().log().all();
		 
		 String json = res.asString();
			JsonPath jsonPath = new JsonPath(json);
			HashMap jobDetails = jsonPath.get("jobDetails");
		String moduleType = jobDetails.get("moduleType").toString();
		
	}

	/* TestCaseId - C19036 */
	@Test(enabled = true, priority = 7)
	public void jobManagerGetJob() {
		
RestAssured.baseURI = "https://dev-runwayjobmanager-e.aims360runway.com";
		
		JsonObject reqObject = new JsonObject();
		 reqObject.addProperty("getBy", "moduleType");
		 reqObject.addProperty("getByValue",moduleType);
		 
		 Response res = expect().statusCode(Integer.parseInt("200")).given()
					.contentType("application/json")
					.header("Authorization", "Bearer " + accessToken)
					.header("x-functions-key", xfunctionKey)
					.body(reqObject.toString())
					.log().everything()
					 .when().get(APITestHelper.API_GETJOBMANAGER_PATH);
		 res.then().log().all();
		
		
	}
}
