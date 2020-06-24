package com.mapquestions;

import java.util.Arrays;
import java.util.List;

public class TestString {

	public static void main(String[] args) {
		String sapemaillist = "chandrika.grs@cogknit.com,maithri.vm@cogknit.com";

		System.out.println(sapemaillist);

		System.out.println(sapemaillist.replaceAll(" ", ""));

		System.out.println(Arrays.asList(sapemaillist.replaceAll(" ", "").split(",")));
		List<String> emails  = Arrays.asList(sapemaillist.replaceAll(" ", "").split(","));
		System.out.println(emails);
		

	}

}
