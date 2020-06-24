package com.example.SeondTest.MainController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String homeMapping() {
		return "welcome to the Home Page of Second Micro Service";
	}

	@GetMapping("/Test1")
	public Map<String, Serializable> getMapping() {

		Map<String, Serializable> hashMap = new HashMap<String, Serializable>();
		hashMap.put("Company", "BhajantriMatrimony");
		hashMap.put("FirstName", "Santosh");

		return hashMap;
	}

}
