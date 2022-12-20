/**
 * 
 */
package com.runway.test.ui.common.utils;


import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parse TestNG failed xml
 * 
 */
public class TestNGFailedList {

	public static void main(String []args) {
		try {
		if (args[0].equalsIgnoreCase("false")) {
			System.out.println("");
		} else {

				String testNgfileDir = System.getProperty("testng.results", "testng-results.xml");
				testNgfileDir = testNgfileDir.replace("testng-results.xml", "testng-failed.xml");
				String failedMethodsListForMVN = getFailedMethodsWithPreviousBuild(testNgfileDir);
				if(failedMethodsListForMVN.trim().contentEquals(""))
				{
					System.out.println("");
				}
				else
				{
					System.out.println("#"+ failedMethodsListForMVN);
				}
				
			} 
		}
		catch (Exception Ex) {
			System.out.println("");
		}
	}

	public static String getFailedMethodsWithPreviousBuild(String file) throws Exception {
		String value = "";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new FileInputStream(file));
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();
			XPathExpression expr = xp.compile("suite/test/classes/class/methods/*");
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				String val = el.getAttribute("name");
				if (!(val.equalsIgnoreCase("setup") || val.equalsIgnoreCase("unsetup")
						|| val.equalsIgnoreCase("beforeMethod"))) {
					if (value.contentEquals("")) {
						value = val;
					} else {
						value = value + "+" + val;
					}
				}
			}
		return value;
	}
}