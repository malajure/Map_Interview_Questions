package com.example.TestApi.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.TestApi.interfaces.converter.evaluators.MimeTypeEvaluatorInterface;
import com.example.TestApi.service.CsvFileUploadService;
import com.example.TestApi.service.FileUploadService;
import com.example.TestApi.service.UserModelService;

@RestController
public class ExcelUploadController {

	private static Log logger = LogFactory.getLog(ExcelUploadController.class);

	@Value("${tempFolder}")
	private String tempFolder;

	@Autowired
	ConfigurableApplicationContext ctx;

	/*
	 * @Autowired FileUploadService fileUploadService;
	 */
	@Autowired
	CsvFileUploadService csvFileUploadService;

	protected MimeTypeEvaluatorInterface mimeTypeEvaluator;

	@PostConstruct
	public void init() {

		System.out.println("loade after dependency is done");
		mimeTypeEvaluator = ctx.getBean(MimeTypeEvaluatorInterface.class);
		System.out.println(mimeTypeEvaluator.toString());
	}

	@PostMapping("/bulkverify")
	public Map<String, Serializable> bulkverify(@RequestParam("uFile") MultipartFile uFile,
			@RequestParam("uData") String uData) {
		// first get file content type
		String mimeType = uFile.getContentType();
		String originalFileName = tempFolder + "/" + uFile.getOriginalFilename();

		// constructor creates a new File instance by converting the given pathname
		// string into an abstract pathname
		File tempFile = new File(originalFileName);

		try {
			// Transfer the received file to the given destination file.
			uFile.transferTo(tempFile);
		} catch (IllegalStateException | IOException e1) {
		}
		// get file Extension
		String fileExtension = FilenameUtils.getExtension(uFile.getOriginalFilename());

		if (!(mimeTypeEvaluator.allowedCSVMimeTypes().contains(mimeType)
				|| mimeTypeEvaluator.allowedExcelMimeTypes().contains(mimeType))) {
			HashMap<String, Serializable> response = new HashMap<String, Serializable>(); // to get the empty list
																							// (immutable).
			response.put("result", (Serializable) Collections.emptyList());
			response.put("responseMsg", "File type not supported. only csv allowed");
			response.put("status", "failed");
			return response;

		}
		// if Error Occurs then call this method deletes the file or directory defined
		// by the abstract path name from storage
		tempFile.delete();

		// String uData="{\"company_id\":\"2\"}";
		// second type

		// return fileUploadService.doBulkVerify(uFile, uData);
		return csvFileUploadService.performBukVerify(uFile, uData);

	}

}
