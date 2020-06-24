package com.example.TestApi.service;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.TestApi.config.ApplicationContext;
import com.example.TestApi.dao.UserStructureDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserStructureService {
	
	// logger
	private static Log logger = LogFactory.getLog(UserStructureService.class);

	@Autowired
	UserStructureDAO userStructureDAO;

	public Map<String, Object> getRegionList(String regionID, Integer records, int pindex, String prefix) {
		logger.debug("regionID:" + regionID);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> resp = new ArrayList<Map<String, Object>>();
		logger.debug("In if no records no pindex");
		resp = (List<Map<String, Object>>) userStructureDAO.getRegion(regionID, prefix, records, pindex, false);

		if (resp.isEmpty()) {
//				result.put("status", "success");
//				result.put("responseMsg", "no region ");
			result.putAll((Map<String, Object>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("structure"))
					.get("region")).get("success")).get("notregionexist"));
			result.put("result", resp);
			result.put("size", 0);
		} else {
			result.put("size", userStructureDAO.getRegion(regionID, prefix, records, pindex, true));
			result.put("status", "success");
//				result.put("responseMsg", "list of regions");
			result.putAll((Map<String, Object>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("structure"))
					.get("region")).get("success")).get("listofregions"));
			result.put("result", resp);
		}

		return result;
	}

}
