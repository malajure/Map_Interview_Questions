package com.example.TestApi.interfaces.converter.evaluators;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.TestApi.service.CsvFileUploadService;
import com.example.TestApi.service.UserModelInterface;
import com.example.TestApi.service.UserModelService;

@Service
@Primary
public class UserChildService extends CsvFileUploadService implements UserModelInterface {

	private Log logger = LogFactory.getLog(UserChildService.class);

	@Override
	public Map<String, Serializable> validatefstname(Map<String, Serializable> props) {
		logger.debug("in child firstname validation");
		if (props.containsKey("emp_name")) {
			String emp_name = (String) props.get("emp_name");
			int x = emp_name.indexOf(" ");
			if (x != -1)
				props.put("firstName", emp_name.substring(0, x));
			else
				props.put("firstName", emp_name);
			logger.debug("firstname: " + props.get("firstName"));

		}
		return super.validatefstname(props);
	}

	@Override
	public Map<String, Serializable> validatelstname(Map<String, Serializable> props) {
		logger.debug("in child lastname validation");
		if (props.containsKey("emp_name")) {
			String emp_name = (String) props.get("emp_name");
			int x = emp_name.indexOf(" ");
			if (x != -1)
				props.put("lastName", emp_name.substring(x + 1));
			else
				props.put("lastName", "");

			logger.debug("lastname: " + props.get("lastName"));

		}
		return super.validatelstname(props);
	}


}
