package com.testRail.testManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;

public class TestRailManager {
     
	//public static String TEST_RUN_ID;https://azuretestrail.testrail.com/
	
	public static String TESTRAIL_USERNAME = "aravindsai0921@gmail.com";
	public static String TESTRAIL_PASSWORD = "HN9cQ1QpUPYgPUE5Ro6a";
	public static String RAILS_ENGINE_URL = "https://testraildemoautomation.testrail.com/";
	public static final int TEST_CASE_PASSED_STATUS = 1;
	public static final int TEST_CASE_FAILED_STATUS = 5;
	
	public static void addResultForTestCase(String testRunId,String testCaseId ,int status,String error) throws IOException, APIException {
		
		
		
		 APIClient client = new APIClient(RAILS_ENGINE_URL);
		    client.setUser(TESTRAIL_USERNAME);
		    client.setPassword(TESTRAIL_PASSWORD);
		    
		    Map data = new HashMap();
		    data.put("status_id", status);
		    data.put("comment", "This test worked fine!");
		    JSONObject r = (JSONObject) client.sendPost("add_result_for_case/"+testRunId+"/"+testCaseId, data);
		    System.out.print(r);
	}
}
	