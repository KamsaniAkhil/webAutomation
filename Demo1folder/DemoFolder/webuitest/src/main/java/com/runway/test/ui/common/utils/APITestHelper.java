package com.runway.test.ui.common.utils;

import static io.restassured.RestAssured.expect;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class APITestHelper {

	public static final String API_HOST = System.getProperty("app.base.url",
			"https://dev-runwayauth-e.aims360runway.com");
	public static final String WEB_UI_CONTEXT = System.getProperty("web.ui.context", "/api");
	public static final String API_LOGIN_PATH = System.getProperty("api.login.path",
			WEB_UI_CONTEXT + "/Authentication");
	public static final String API_WAREHOUSES_PATH = System.getProperty("api.warehouses.path",
			WEB_UI_CONTEXT + "/GetWarehouses");
	public static final String API_CREATEJOBMANAGER_PATH = System.getProperty("api.warehouses.path",
			WEB_UI_CONTEXT + "/CreateJob");
	public static final String API_GETJOBMANAGER_PATH = System.getProperty("api.warehouses.path",
			WEB_UI_CONTEXT + "/GetJobs");

	public static final String API_BaseURL = System.getProperty("app.base.url", "https://apiwest.aims360.rest");
	public static final String API_POPATH = System.getProperty("api.purchaseOrder.path",
			API_BaseURL + "/wip/v1.0/PurchaseOrders");
	public static final String LOGIN_API_URL_East = System.getProperty("app.url.east",
			"https://dev-runwayauth-e.aims360runway.com");
	public static final String LOGIN_API_URL_West = System.getProperty("app.url.west",
			"https://dev-runwayauth-w.aims360runway.com");
	public static final String RUNWAY_JobManager_URI = System.getProperty("api.jobmanager.url",
			"https://aims360-fn-w2-dev-runwayjobmanager.azurewebsites.net/");
	public static final String RUNWAY_JobManager_Path = System.getProperty("api.jobmanager.path", "/api" + "/GetJobs");
	public static final String RUNWAY_MondayCom_Path = System.getProperty("api.mondaycomservice.path",
			WEB_UI_CONTEXT + "/RunwayModayComService");
	public static final String RUNWAY_SettingsService_Path = System.getProperty("api.settingsservice.path",
			WEB_UI_CONTEXT + "/SettingsService");
	public static final String RUNWAY_Mondaycom_URI = System.getProperty("api.mondaycom.url",
			"https://api.monday.com/");
	public static final String RUNWAY_Version_Path = System.getProperty("api.version.path", "v2");
	public static final String RUNWAY_PurchaseOrder_Path = System.getProperty("api.purchase.path",
			"/wip/v1.0/purchaseorder/50101");
	public static final String RUNWAY_SettingsService_URI = System.getProperty("api.settingsSerivce.url",
			"https://runwaysettings-w.aims360runway.com");
	public static final String RUNWAY_CUSTOMERDETAILS_PATH = System.getProperty("api.customerDetails.path",
			"/orders/v1.0/orders");
	public static final String RUNWAY_STYLE_PATH = System.getProperty("api.style.path", "/styles/V1.0/style/");

	private String accessToken = null;
	private String clientCode = null;
	private String securityCode = null;
	private String zone = null;
	public static final String x_functions_key = "gUt2dGAafsiYeI6za6fHJfbr0uyhl5QDMZox0iF_F3VxAzFuwei5rA==";
	private Response response = null;

	public static final int HTTP_STATUS_OK = 200;
	public static final int HTTP_STATUS_CREATED = 201;

	Logger logger = LoggerFactory.getLogger(APITestHelper.class);
	public Object res;

	public APITestHelper() {
		logger.info(">>>Setting base uri..." + API_HOST);
		RestAssured.baseURI = API_HOST;
		RestAssured.basePath = "/";
	}

	public Response doLogin(JsonObject reqObject, String expStatusCode) {

		RestAssured.baseURI = API_BaseURL;
		Response res = expect().statusCode(Integer.parseInt(expStatusCode)).given().contentType("application/json")
				.header("x-functions-key", x_functions_key).body(reqObject.toString()).log().everything()

				.when().post(API_LOGIN_PATH);
		res.then().log().all();
		response = res;
		if (expStatusCode.equals(getIntValue(res, "statusCode") + "")) {

			clientCode = getStringValue(res, "clientCode");

			logger.info("After login--> clientCode =" + clientCode);
		}
		return res;
	}

	public Response doLogin(JsonObject reqObject) {
		return doLogin(reqObject, "200");
	}

	public Response auth(JsonObject reqObject, String expStatusCode) {

		RestAssured.baseURI = LOGIN_API_URL_East;
		Response res = expect().statusCode(Integer.parseInt(expStatusCode)).given().contentType("application/json")
				.header("x-functions-key", x_functions_key).body(reqObject.toString()).log().everything()
				.when().post(API_LOGIN_PATH);
		res.then().log().all();
		response = res;
		if (expStatusCode.equals(getIntValue(res, "statusCode") + "")) {

			clientCode = getStringValue(res, "clientCode");

			logger.info("After login--> securityCode =" + securityCode);
			logger.info("After login ---> Zone = " + zone);
		}
		return res;
	}

	public String getStringValue(Response res, String key) {

		String json = res.asString();
		JsonPath jsonPath = new JsonPath(json);
		return jsonPath.get(key);

	}

	/**
	 * Get a key's HashMap value from response body
	 * 
	 * @param res
	 * @param key
	 * @return
	 */

	public HashMap getArrayValue(Response res, String key) {

		String json = res.asString();
		JsonPath jsonPath = new JsonPath(json);
		return jsonPath.get(key);

	}

	/**
	 * Get a key's string value from response body
	 * 
	 * @param res
	 * @param key
	 * @return
	 */

	public String getStringValue(ResponseBody res, String key) {

		String json = res.asString();
		JsonPath jsonPath = new JsonPath(json);
		return jsonPath.get(key);

	}

	/**
	 * Get a key's int value from response
	 * 
	 * @param res
	 * @param key
	 * @return
	 */
	public int getIntValue(Response res, String key) {

		String json = res.asString();
		JsonPath jsonPath = new JsonPath(json);
		return (Integer) (jsonPath.get(key));

	}
}
