package com.example.TestApi.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMap {

	// logger
	private static Log logger = LogFactory.getLog(TestMap.class);

	public static void main(String[] args) {

		TestMap ref = new TestMap();
		Map<String, Serializable> result = ref.performBukVerify("{\"company_id\":\"1\"}");
		System.out.println(result);

	}

	@SuppressWarnings("unchecked")
	public Map<String, Serializable> performBukVerify(String uSentData) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		// map to get the json data
		Map<String, Serializable> props = new HashMap<String, Serializable>();

		// check userSent Json Data
		// convert String to Map Key = value pair
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			props = (Map<String, Serializable>) objectMapper.readValue(uSentData, Map.class);
			System.out.println(props);
		} catch (Exception e) {
			logger.debug("Error is json to map conversion" + e.getLocalizedMessage());
		}

		return result;

	}

}
