package com.masm.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.masm.Dto.MessageResponse;
import com.masm.Dto.UserRole;
import com.masm.model.Role;
import com.masm.service.RoleService;
import com.masm.service.UserService;

@RestController
public class HomeController {

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/user")
	public String getUserHome() {
		return "UserHome";
	}

	@GetMapping("/admin")
	public String getAdminHome() {
		return "Admin Home";
	}

	@GetMapping("/moderator")
	public String getTenateHome() {
		return "Moderator Home";
	}

	@GetMapping("/")
	public String homePage() {
		return "HOME";
	}

	@SuppressWarnings("unchecked")
	@PostMapping(path = "/register")
	public ResponseEntity<?> createUser(@Valid @RequestBody Map<String, Serializable> uData) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		uData.put("active", 1);
		uData.put("password", passwordEncoder.encode(uData.get("password").toString()));

		if (userService.existsByUsername(uData.get("username").toString())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userService.existsByEmail((String) uData.get("email"))) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		List<String> roles = (List) uData.get("role");
		Set<String> roleSet = new HashSet<>();

		String userRole = "";
		if (roles == null) {
			userRole = roleService.getRoleByUserName(UserRole.ROLE_USER);
			roleSet.add(userRole);
		} else {
			roles.forEach(role -> {
				switch (role) {
				case "admin":
					roleSet.add(roleService.getRoleByUserName(UserRole.ROLE_ADMIN));
					break;
				case "mod":
					roleSet.add(roleService.getRoleByUserName(UserRole.ROLE_MODERATOR));
					break;
				default:
					roleSet.add(roleService.getRoleByUserName(UserRole.ROLE_USER));
					break;
				}
			});
		}
		uData.put("role",(Serializable) roleSet);

		Map<String, Serializable> resultMap = userService.createUser(uData);

		if (!resultMap.containsKey("success"))
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		else
			return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}

}
