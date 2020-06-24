package com.mapiterations;

import java.util.ArrayList;
import java.util.List;

public class TestMap2 {

	public static void main(String[] args) {

		List<Integer> numbers = new ArrayList<Integer>();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);

		System.out.println(numbers);
		
		List<Integer> numbers2 = new ArrayList<Integer>(4);
		System.out.println(numbers2);

	}

}
