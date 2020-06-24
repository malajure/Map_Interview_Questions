package com.cogknit.user.parent.service;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cogknit.interfaces.user.Service.UserModelInterface;

@Service
public class UserModelService implements UserModelInterface {

	protected static Log logger = LogFactory.getLog(UserModelService.class);

	

	@Override
	public Map<String, Serializable> bulkverify(MultipartFile file, String uData) {
		logger.info("from User Parent service");

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		return result;
	}

}
