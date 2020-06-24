package com.example.Map_Questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;
import java.util.*;

public class TwoListToSIngleMap {

	public static void main(String[] args) {

		// Header List
		List<String> headerKeys = Arrays.asList("PS_NO", "FIRST_NAME", "LAST_NAME", "GENDER", "PHONE", "EMAIL",
				"DEPARTMENT_ID", "DEPARTMENT_NAME", "DESIGNATION_ID", "DESIGNATION_NAME", "EMPLOYMENT_STATUS",
				"INDEPENDENT_COMPANY", "SBU", "BAND", "GRADE", "LOCATION");

		// rows list
		List<String> rowsData = Arrays.asList("123", "Sant", "Leaner", "Dev", "AAAA", "M", "5555", "MMM", "RR", "RRR",
				"HHH", "HHH", "HH", "HHH", "HH","anil");

		List<Map<String, Object>> preProcessinglist = new ArrayList<Map<String, Object>>();
		Map<String, Object> row = null;
 
		 if(headerKeys.size() ==  rowsData.size())
		for (int i = 0; i < headerKeys.size(); i++) {
			if (headerKeys.get(i) != null && rowsData.get(i) != null) {
				row = new HashMap<String, Object>();
				row.put(headerKeys.get(i), rowsData.get(i));
				preProcessinglist.add(row);
			}
		}
		
		System.out.println(preProcessinglist);
	}

}
