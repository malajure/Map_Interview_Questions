package com.cogknit.interfaces.converter.evaluators;

import java.io.IOException;
import java.util.List;

public interface MimeTypeEvaluatorInterface {
	List<String> getMimeTypeList();

	String getFileExtension(String fileName);

	boolean checkForValidFileFormat(String fileName, String fileExtension) throws IOException;

	List<String> allowedCSVMimeTypes();

	List<String> allowedExcelMimeTypes();

	List<String> allowedPDFMimeTypes();

	List<String> allowedImageMimeTypes();
}
