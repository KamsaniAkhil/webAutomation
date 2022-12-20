/**
 * 
 */
package com.runway.test.ui.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;


/**
 * A simple test data supplier
 * 
 */
public class TestDataProvider {
	static Logger logger = LoggerFactory.getLogger(TestDataProvider.class);
	static String CSV_SEPARATOR = System.getProperty("separator.testdata",":-:");
	public static final String TEST_HOME = System.getProperty("test.home",".."+File.separator+"..");
	private static final String TEST_PROPERTY_FILE = "test.properties";

	@DataProvider
	public static Object[][] TestData() {
	return getTestData("TestFile");
	}

	/**
	 * Read CSV data file
	 * 
	 * @param dataFile
	 * @param nrows
	 * @param ncols
	 * @return
	 */
	public static Object[][] readCsvDataStore(String dataFile) {
		Object[][] data = null;
		BufferedReader reader = null;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("datafiles/"+dataFile);
			File file = new File(url.getPath());
			logger.info(">>>Reading csv - "+file.getAbsolutePath());
			data = readCsvFile(file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
		
	}

	/**
	 * Read CSV data file
	 * 
	 * @param dataFile
	 * @param nrows
	 * @param ncols
	 * @return
	 */
	public static Object[][] readCsvFile(String dataFile) {
		Object[][] data = null;
		BufferedReader reader = null;
		try {
			File file = new File(dataFile);
			logger.info(">>>Reading csv - "+file.getAbsolutePath());
			//reader = new BufferedReader(new FileReader("datafiles"+File.separator+dataFile));
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int row = 0;
			int nrows = 0;
			int ncols = 0;
			BufferedReader reader1 = new BufferedReader(new FileReader(file));
			while ((line=reader1.readLine())!=null) {
				if (!(line.startsWith("#"))&&(!"".equals(line.trim()) && (line.trim().length()!=0))) {
					nrows++;
					if (ncols==0) {
						ncols = line.split(CSV_SEPARATOR).length;
					}
				}
			}
			data = new Object[nrows][ncols]; 
			line = null;
			while ((line=reader.readLine())!=null) {
				logger.info("<<< data line: "+line+", length:"+line.trim().length());
				if (!(line.startsWith("#"))&&(!"".equals(line.trim()) && (line.trim().length()!=0))) {
					logger.info("<<< data line: "+line+", length:"+line.trim().length());
					data[row++] = line.split(CSV_SEPARATOR);
				}
			}
			reader1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
		
	}
	
	/**
	 * Get test specific data
	 * @param testName
	 * @return
	 */
	public static Object[][] getTestData(String testName) {
		String dataFile = System.getProperty(testName+".testdata", testName+"-testdata.csv");
		Object [][] data = readCsvDataStore(dataFile);
		return data;
	}
	
	/**
	 * Get given file data
	 * @param fileName
	 * @return
	 */
	public static Object[][] getFileData(String testName, String fileName) {
		String dataFile = System.getProperty(testName+".testdata", fileName);
		Object [][] data = readCsvFile(dataFile);
		return data;
	}
	
}
