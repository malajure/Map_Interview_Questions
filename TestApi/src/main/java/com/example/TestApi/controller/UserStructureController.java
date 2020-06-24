package com.example.TestApi.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TestApi.service.UserStructureService;

@RestController
@RequestMapping("/icog/services/structure")
public class UserStructureController {

	@Autowired
	private UserStructureService userStructureService;

	private Log logger = LogFactory.getLog(UserStructureController.class);

	@GetMapping("/region")
	public Map<String, Object> getRegion(@RequestParam(value = "regionID", required = false) String regionID,
			@RequestParam(value = "records", required = false, defaultValue = "0") int records,
			@RequestParam(value = "pindex", required = false, defaultValue = "0") int pindex,
			@RequestParam(value = "prefix", required = false, defaultValue = "") String prefix) {
		logger.info("inside get report api for getting list of regions");
		return userStructureService.getRegionList(regionID, records, pindex, prefix);
	}
}
