package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/icog/services")
public class HomeController {

	@GetMapping("/hello")
	public String getTest() {
		return "Hello Welcome";
	}

	@GetMapping("/Second")
	public String getSecond() {
		return "Second Page";
	}

}
