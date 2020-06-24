package com.example.TestApi.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.TestApi.interfaces.converter.evaluators.MimeTypeEvaluatorInterface;
import com.example.TestApi.service.UserModelService;

@RestController
public class HomeController {

	private static Log logger = LogFactory.getLog(HomeController.class);

	@Autowired
	ConfigurableApplicationContext ctx;

	@Autowired
	UserModelService userModelService;

	protected MimeTypeEvaluatorInterface mimeTypeEvaluator;

	@PostConstruct
	public void init() {

		System.out.println("loade after dependency is done");
		mimeTypeEvaluator = ctx.getBean(MimeTypeEvaluatorInterface.class);
		System.out.println(mimeTypeEvaluator.toString());
	}

	

	@GetMapping("/userTemplate")
	public List<String> getBulkHeaderFields() {

		logger.debug("in userTemplate v3");
		return userModelService.getHeaderFields();
	}

	// bulk upload verify
	

	// Bulk Compentency Upload
	/*
	 * public List<List<String>> getCompentencyBulkHeaderFields() {
	 * logger.debug("in v2 competencyTemplate"); }
	 */
}
