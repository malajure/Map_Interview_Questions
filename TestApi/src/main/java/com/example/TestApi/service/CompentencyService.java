package com.example.TestApi.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CompentencyService {

	private static Log logger = LogFactory.getLog(CompentencyService.class);

	@Value("#{${bulk_Competencies_Header}}")
	private Map<String, String> bulkCompetenciesHeader;

	/*
	 * public List<List<String>> getCompentencyBulkHeaderFields() {
	 * Collection<String> allowedHeaderKeys = bulkCompetenciesHeader.values(); //
	 * get key value for CSV file type List<List<String>> templatelist = new
	 * ArrayList<List<String>>(); // List of List List<String> headerList = new
	 * ArrayList<String>(allowedHeaderKeys); templatelist.add(headerList); // add
	 * headerlit to template list
	 * 
	 * }
	 */
}
