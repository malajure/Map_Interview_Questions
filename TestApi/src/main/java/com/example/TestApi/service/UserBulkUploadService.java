package com.example.TestApi.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBulkUploadService {

	@Autowired
	CsvFileUploadService csvFileUploadService;

	private Log logger = LogFactory.getLog(UserBulkUploadService.class);

	public Map<String, Serializable> addBulkUsers(List<Map<String, Serializable>> userList) {
		List<Map<String, Serializable>> returnList = new ArrayList<Map<String, Serializable>>();
		Map<String, Serializable> response = new HashMap<String, Serializable>();

		for (Map<String, Serializable> userMap : userList) {
			Map<String, Serializable> uMap = new HashMap<String, Serializable>();
			Map<String, Serializable> result = null;
			Map<String, Serializable> userMap2 = null;
			try {
				if (userMap.containsKey("reason"))
					userMap.remove("reason");
				if (userMap.containsKey("dataStatus"))
					userMap.remove("dataStatus");
				userMap2 = (Map<String, Serializable>) userMap.get("userData");
				result = csvFileUploadService.verifyAccountV2(userMap2, true);
				logger.debug("result is in addbulk" + result);
				if (((String) result.get("status")).equalsIgnoreCase("success")) {
					uMap.put("reason", "success");
					uMap.put("dataStatus", "success");
				} else {
					uMap.put("reason", result.get("responseMsg"));
					uMap.put("dataStatus", result.get("status"));
				}

				if (result.containsKey("flag") && result.get("flag").toString().equals("created")) {

					uMap.put("flag", result.get("created"));
					uMap.put("reason", result.get("responseMsg"));
					uMap.put("dataStatus", result.get("status"));

				} else if (result.containsKey("flag") && result.get("flag").toString().equals("updated")) {

					uMap.put("flag", result.get("updated"));
					uMap.put("reason", result.get("responseMsg"));
					uMap.put("dataStatus", result.get("status"));

				}

				uMap.put("userData", (Serializable) userMap2);
				returnList.add(uMap);
			} catch (Exception e) {
				uMap.put("userData", (Serializable) userMap2);
				uMap.put("reason", e.getMessage());
				uMap.put("dataStatus", "failed");
				returnList.add(uMap);
			}
			uMap = null;
			result = null;
			userMap = null;
			// TimeUnit.SECONDS.sleep(1);
		}
		logger.debug("return list is" + returnList);
		response.put("results", (Serializable) returnList);
		response.put("status", "success");
		response.put("responseMsg", "success");
		return response;
	}
}
