package com.mapiterations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapInterations {

	public static void main(String args[]) {

		// map has threee implentations
		Map<String, String> httpErros = new HashMap<String, String>();
		httpErros.put("100", "Continue");
		httpErros.put("200", "Ok Reponse");
		httpErros.put("300", "Redirection Error Message");
		httpErros.put("400", "Client Erros");
		httpErros.put("500", "Server Related Errors");

		Map<String, String> KeysWithDescription;
		Map<String, String> extraKeysWithDescription;
		// Map -> key value association and lookup opearations can be done using the
		// keys
		// Map - > { key = value }

		System.out.println(httpErros);
		KeysWithDescription = httpErros;
		extraKeysWithDescription = httpErros;
		// get method is used to get the associated value with Key

		
		
		List<Map<String,Serializable>> returnList = new ArrayList<Map<String,Serializable>>();
		Map<String,Serializable> response = new HashMap<String,Serializable>();
		response.put("100", "Continue");
		response.put("200", "Ok Reponse");
		response.put("300", "Redirection Error Message");
		response.put("400", "Client Erros");
		response.put("500", "Server Related Errors");
		returnList.add(response);
		System.out.println(returnList);

	}
}
