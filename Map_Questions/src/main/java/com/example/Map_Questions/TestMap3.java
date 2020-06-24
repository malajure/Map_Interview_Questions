package com.example.Map_Questions;
import java.io.Serializable;
import java.util.*;

public class TestMap3 {

	public static void main(String[] args) {
		
		//Create a Json String in Java using escape sequence
		String jsonString = "{\"company\":\"Benga\",\"firstName\":\"Shiva\",\"Location\":\"Belgaum\"}";
		
		System.out.println(jsonString);
		
		
		Map<String,Serializable> map = new HashMap<String, Serializable>();
		
		
		map.put("name","Shiva");
		map.put("firstName","Anna");
		
		System.out.println(map);
		
		
	}

}
