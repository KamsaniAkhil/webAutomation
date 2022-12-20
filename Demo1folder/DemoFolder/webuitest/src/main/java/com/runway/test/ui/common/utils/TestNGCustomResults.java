/**
 * 
 */
package com.runway.test.ui.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG custom results
 */
public class TestNGCustomResults implements ITestListener{
	Logger logger = LoggerFactory.getLogger(TestNGCustomResults.class);

	@Override
	public void onTestStart(ITestResult result) {
		logger.info("-->Starting "+result.getMethod().getMethodName());
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		logger.info("-->Success "+result.getMethod().getMethodName());
		
	}

	@Override
	public void onTestFailure(ITestResult result) {
		logger.info("-->Failure "+result.getMethod().getMethodName());
		
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		logger.info("-->Skipped "+result.getMethod().getMethodName());
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		logger.info("-->Failed "+result.getMethod().getMethodName());
		
	}

	@Override
	public void onStart(ITestContext context) {
		logger.info("-->Starting "+context.getName());
		
	}

	@Override
	public void onFinish(ITestContext context) {
		logger.info("-->Finish "+context.getName());
		logger.info("*** PASSED TEST CASES ***");
        context.getPassedTests().getAllResults().forEach(result -> {logger.info(result.getName());});
         
        logger.info("*** FAILED TEST CASES ***");
        context.getFailedTests().getAllResults().forEach(result -> {logger.info(result.getName());});
         
        logger.info("Test completed on: " + context.getEndDate().toString());
	}

}
