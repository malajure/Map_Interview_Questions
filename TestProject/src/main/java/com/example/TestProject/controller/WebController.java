package com.example.TestProject.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

	private static Log logger = LogFactory.getLog(WebController.class);

	@GetMapping("/hello")
	public String helloMessage() {
		logger.debug("inside the heelo");
		return "Hello Welcome";
	}

	@GetMapping("/Second")
	public List<String> getSecond() {

		logger.debug("inside Second");
		List<String> stringList = new ArrayList<String>();
		stringList.add("Sani");
		stringList.add("nee");
		stringList.add("annu");

		logger.debug("list is --->" + stringList);
		return stringList;
	}

}
