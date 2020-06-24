package com.example.TestApi.interfaces.converter.evaluators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MimeTypeEvaluator implements MimeTypeEvaluatorInterface {

 	private static final Logger LOG = LoggerFactory.getLogger(MimeTypeEvaluator.class);

	public final String CSV = "text/csv,application/csv";
	public final String XLS = "application/excel,application/vnd.ms-excel,application/x-excel,application/x-msexcel";
	public final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	@Override
	public List<String> getMimeTypeList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileExtension(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkForValidFileFormat(String fileName, String fileExtension) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> allowedCSVMimeTypes() {
		return Arrays.asList(CSV.split(","));
	}

	@Override
	public List<String> allowedExcelMimeTypes() {
		List<String> mimeTypes = new ArrayList<String>();
		mimeTypes.addAll(Arrays.asList(XLS.split(",")));
		return mimeTypes;
	}

	@Override
	public List<String> allowedPDFMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> allowedImageMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
