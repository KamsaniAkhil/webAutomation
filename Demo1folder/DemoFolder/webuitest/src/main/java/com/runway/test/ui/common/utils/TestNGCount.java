/**
 * 
 */
package com.runway.test.ui.common.utils;

import java.io.FileInputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * Parse TestNG result xml
 */
public class TestNGCount {
	
	public static void main(String []args) {
		Map<String, Integer> counts = getResultCounts(System.getProperty("testng.results","testng-results.xml"));
		System.out.println(counts);
	}
	
	
	public static String getXPathVal(String file, String expression) {
		String value = null;
		try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document doc = builder.parse(new FileInputStream(file));
				XPathFactory xpf = XPathFactory.newInstance();
				XPath xp = xpf.newXPath();
				XPathExpression expr = xp.compile(expression);
				value = expr.evaluate(doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static Map<String, Integer> getResultCounts(String xmlFile) {
		Map<String, Integer> counts = new TreeMap<String, Integer>();
		//skipped="0" failed="1" total="7" passed="6"
		String sPassCount = getXPathVal(xmlFile, "/testng-results/@passed");
		String sFailCount = getXPathVal(xmlFile, "/testng-results/@failed");
		String sSkipCount = getXPathVal(xmlFile, "/testng-results/@skipped");
		String sTotalCount = getXPathVal(xmlFile, "/testng-results/@total");
		int nPassCount = Integer.parseInt(sPassCount);
		int nFailCount = Integer.parseInt(sFailCount);
		int nSkipCount = Integer.parseInt(sSkipCount);
		if ((sPassCount!=null) &&(sPassCount!="")) {
			counts.put("Passed", nPassCount);
		}
		if ((sFailCount!=null) &&(sFailCount!="")) {
			counts.put("Failed", nFailCount);
		}
		if ((sSkipCount!=null) &&(sSkipCount!="")) {
			counts.put("Skipped", nSkipCount);
		}
		if ((sTotalCount!=null) &&(sTotalCount!="")) {
			int nTotal = Integer.parseInt(sTotalCount);
			int aTotal = nPassCount+nFailCount+nSkipCount;
			if (nTotal>aTotal) {
				counts.put("TIP/Commented", (nTotal-aTotal));
			}
			counts.put("Total", nTotal);
			
		}
		return counts;
	}
	


}
