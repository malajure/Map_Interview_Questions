package com.example.Map_Questions;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TestMap2 {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Serializable> props = null;
		String data2 = "{\"name\":\"Raju\",\"company_id\":\"3000\"}";

		ObjectMapper mapper = new ObjectMapper();
		props = (Map<String, Serializable>) mapper.readValue(data2, Map.class);
   
		
		System.out.println(data2);
		System.out.println(props);
		
		Serializable value = props.get("type");
		
		
	

	}

}
