
package com.example.TestApi.config;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAutoConfiguration
public class ApplicationContext implements EnvironmentAware {

	protected Log logger = LogFactory.getLog(getClass());

	private static Environment environment;

	public static Map<String, Object> jsonObject = null;

	@PostConstruct
	public void jsonObjectLoder() {
		jsonObject = getResponseString();
	}

	@Override
	public void setEnvironment(Environment environment) {
		ApplicationContext.environment = environment;
	}

	public static String getGlobalProperty(String propertyName) {
		return environment.getProperty(propertyName);
	}

	protected Map<String, Object> getResponseString() {
		if (jsonObject == null || jsonObject.isEmpty()) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				jsonObject = objectMapper.readValue(
						ApplicationContext.class.getClassLoader().getResourceAsStream("icogResponse.json"), Map.class);
			} catch (IOException e) {
				logger.error("IO exception ");
			}
			objectMapper = null;
		}
		return jsonObject;
	}

}
