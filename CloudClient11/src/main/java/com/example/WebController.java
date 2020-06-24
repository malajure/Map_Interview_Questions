package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {
	
	@Value("#{sapemaillist}")
	private String mail;
	
	@GetMapping
	public String mail() {
		return mail;
	}

}
