package com.example.TestApi.service;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.supercsv.io.Tokenizer;
import org.supercsv.prefs.CsvPreference;

public class SkipBlankLinesTokenizer extends Tokenizer {
	private Log logger = LogFactory.getLog(SkipBlankLinesTokenizer.class);

	public SkipBlankLinesTokenizer(Reader reader, CsvPreference preferences) {
		super(reader, preferences);
	}

	@Override
	public boolean readColumns(List<String> columns) throws IOException {

		boolean moreInput = super.readColumns(columns);

		// keep reading lines if they're blank
		//logger.debug("getPreferences().getDelimiterChar() "+(char)getPreferences().getDelimiterChar());

		while (moreInput && (getUntokenizedRow().replaceAll("["+(char)getPreferences().getDelimiterChar()+"]", "").trim().isEmpty())) {

			moreInput = super.readColumns(columns);
		}

		return moreInput;
	}

}