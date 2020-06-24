package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondCOntroller {

	@GetMapping("/fff")
	String homePage() {
		return "Welcome Second Home PAge";
	}
}
