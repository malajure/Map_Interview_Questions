package com.cogknit.interfaces.user.Service;

import java.io.Serializable;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface UserModelInterface {

	Map<String, Serializable> bulkverify(MultipartFile file, String uData);
}
