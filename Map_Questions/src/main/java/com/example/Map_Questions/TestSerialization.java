package com.example.Map_Questions;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TestSerialization {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Serializable> props = null;

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("company", "3000");

		String data2 = "{\"name\":\"Raju\",\"company_id\":\"3000\"}";

		ObjectMapper mapper = new ObjectMapper();
		props = (Map<String, Serializable>) mapper.readValue(data2, Map.class);

		System.out.println(props);

		// key
		boolean check = props.containsKey("type");
		
		System.out.println(props.containsKey("company_id") ) ;
		Serializable company_ser =props.containsKey("company_id") ? (Serializable) props.get("company_id") : 0;
		System.out.println(company_ser);
		
		
		List<Map<String, Serializable>> returnList = new ArrayList<Map<String, Serializable>>(1);
		System.out.println(returnList);

	}

}
