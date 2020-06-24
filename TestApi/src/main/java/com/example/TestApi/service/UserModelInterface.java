package com.example.TestApi.service;

import java.io.Serializable;
import java.util.Map;

public interface UserModelInterface {
	Map<String, Serializable> validatefstname(Map<String, Serializable> userMap);

	Map<String, Serializable> validatelstname(Map<String, Serializable> userMap);


}
