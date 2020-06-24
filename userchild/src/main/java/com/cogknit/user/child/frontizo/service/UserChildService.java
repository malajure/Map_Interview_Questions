package com.cogknit.user.child.frontizo.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cogknit.interfaces.user.Service.UserModelInterface;
import com.cogknit.user.parent.service.UserModelService;

@Service
@Primary
public class UserChildService extends UserModelService implements UserModelInterface {

	private static Log logger = LogFactory.getLog(UserChildService.class);
	
	/*
	 * @Override public Map<String, Serializable> bulkverify(MultipartFile file,
	 * String uData) { logger.info("from User Frontizo service");
	 * 
	 * Map<String, Serializable> result = new HashMap<String, Serializable>();
	 * return result; }
	 */
}
