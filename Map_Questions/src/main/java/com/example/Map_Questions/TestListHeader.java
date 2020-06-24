package com.example.Map_Questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestListHeader {

	public static void main(String[] args) {
		List<String> headerData = Arrays
				.asList("Sl No.,Emp Code,Employee Name,Level,Job Title,HRMS ID,Cost Center,Job Code,"
						+ "Job Classification,Job Family,Manager Emp ID,Current Manager,Department (for LMS),"
						+ "DOJ (Appario/Frontizo),Entity Name,Building / location Name,SBU,Gender,Mobile Number,"
						+ "OFFICIAL EMAIL ID");

		
		
		List<String> headerData2 = new ArrayList<String>
		(Arrays
				.asList("Sl No","Emp COde"));
		System.out.println(headerData2);
		System.out.println(headerData2.size());
		Map<String, Object> row = null;
		

	}

}
