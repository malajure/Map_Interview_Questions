package com.cogknit.user.parent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/icog/services/user")
public class UserModelController {
	
	@GetMapping("/parent")
	public String getParent() {
		return "Parent";
	}

}
