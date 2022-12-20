/**
 * 
 */
package com.runway.test.ui.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class PropsHandler {
	static Logger logger = LoggerFactory.getLogger(PropsHandler.class);	
	
	/**
	 * Get the properties from a resource file and overwrite with system properties
	 * 
	 * @param fileName
	 * @return
	 */
	 public static Properties getProperties(String fileName){

         Properties p = new Properties();
        
         // Load the properties file
         try {
			URL url = Thread.currentThread().getContextClassLoader()
					.getResource(fileName);
			logger.info("Properties file "+fileName);			
			InputStream resIs = null;
			if (url != null) {
				logger.info("Properties file path="+url.getPath());
				File f = new File(url.getPath());
				if (f.canRead()) {
					resIs = new FileInputStream(f);
					p.load(resIs);
				} else {
					logger.warn("No properties file " + fileName + " found!");
				}
			}
         } catch (Exception e) {
                 logger.warn("Error while loading properties!");
                 e.printStackTrace();
         }
         // Update with system properties
         for (Object name: p.keySet()) {
                 String val = System.getProperty((String)name);
                 if (val!=null) {
                         p.put((String)name, val);
                 }
         }
         Properties sys = System.getProperties();
         for (Object name: sys.keySet()){
        	 //logger.info(name+"="+sys.get(name));
        	 p.put(name, sys.get(name));
         }
         return p;
	 }
	 
	
}
