package com.mapiterations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestMap {

	public static void main(String[] args) {

		List<String> strings = Arrays.asList("shiva", "deva", "anna");
		System.out.println(strings);

		// old approach
		List<String> oldResult = new ArrayList<String>();
		for (String element : strings) {
			oldResult.add(element.toUpperCase());

		}
		List<String> result = strings.stream().map(String::toUpperCase).collect(Collectors.toList());

		System.out.println(oldResult);
		System.out.println(result);

	}

}
