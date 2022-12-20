package com.runway.test.ui.common.utils;


import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Class that Serializes and Deserializes LocalTime values into and from JSON, respectively.
 */
public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

	private static final Logger LOGGER = Logger.getLogger(LocalTimeAdapter.class.getName());
	
    public LocalTimeAdapter() {
    	
    }

	@Override
	public LocalTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) 
			throws JsonParseException {
		LocalTime localTime = null;
		try {			
			String localTimeStr = jsonElement.getAsString();
			//String localTimeStr = jsonElement.getAsJsonPrimitive().getAsString();
			//LOGGER.info(String.format("DE-Serialize JSON [%s] ", localTimeStr));	
			localTime =  LocalTime.parse(localTimeStr, DateTimeFormatter.ISO_LOCAL_TIME);
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw ex;
		}
		return localTime;
	}

	@Override
	public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext jsonSerializationContext) {
		try {
			//LOGGER.info(String.format("LocaltTime - %s into String %s", localTime, localTime.format(DateTimeFormatter.ISO_LOCAL_TIME)));
			return new JsonPrimitive(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw ex;
		}
	}
	
//	/** Formatter. */
//	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
//
//	@Override
//	public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
//		return new JsonPrimitive(FORMATTER.format(src));
//	}
//
//	@Override
//	public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//			throws JsonParseException {
//		return FORMATTER.parse(json.getAsString(), LocalTime::from);
//	}
	  
}
