package com.mapquestions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TestMap {

	public static void main(String[] args) {
		String company_nm = "";
		Map<String, Serializable> response = new HashMap<String, Serializable>();
		Map<String, Serializable> postprocessingresult = getDBResponse();

		System.out.println(postprocessingresult);

		company_nm = postprocessingresult != null && postprocessingresult.containsKey("company_nm")
				? (String) postprocessingresult.get("company_nm")
				: company_nm;

		response.put("company_txt", company_nm);
		System.out.println("response ----> " + response);
		
		
	}

	public static Map<String, Serializable> getDBResponse() {
		Map<String, Serializable> response = new HashMap<String, Serializable>();

		response.put("company_nm", "Frontizo Business Services Private Ltd");

		return response;
	}
	
	public void sendUserSapUpdate(String toemail, Map<String, Serializable> response) {
		
	}

}
