package com.example.TestApi.controller;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TestApi.config.ApplicationContext;
import com.example.TestApi.service.UserBulkUploadService;

@RestController
@RequestMapping("/icog/services/user")
public class BulkUploadController {

	private static Log logger = LogFactory.getLog(BulkUploadController.class);

	@Autowired
	UserBulkUploadService userBulkUploadService;

	// ? is a wild card represents any posiible object as a Value in a Map :used in
	// the generic Type
	@PostMapping("/bulk")
	public Map<String, Serializable> addBulkUsers(@RequestBody Map<String, ?> uMap) {

		logger.debug("inside the Controller");
		// check for null
		if (uMap == null || uMap.isEmpty() || (((List) (uMap.get("results"))).size() < 1)) {

			Map<String, Serializable> response = new HashMap<String, Serializable>();

			response.putAll((Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
					.get("bulk")).get("failed")).get("notsuppliedprops"));

			response.put("results", (Serializable) Collections.EMPTY_LIST);
			return response;

		}

		// convert uMap to List type
		List<Map<String, Serializable>> userList = ((List<Map<String, Serializable>>) uMap.get("results"));

		return userBulkUploadService.addBulkUsers(userList);

	}

}
