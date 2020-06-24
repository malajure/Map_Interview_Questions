package com.masm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String homePage() {
		return ("<h4>Home</h4>");
	}

	@GetMapping("/user")
	public String userPage() {
		return ("<h3>User Home Page</h3>");
	}
	@GetMapping("/admin")
	public String userAdmin() {
		return ("<h5>Admin Page</h5>");
	}

}
