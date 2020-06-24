package com.mapiterations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapIterate {
	public static void main(String[] args) {

		Map<String, String> httpErros = new HashMap<String, String>();
		httpErros.put("100", "Continue");
		httpErros.put("200", "Ok Reponse");
		httpErros.put("300", "Redirection Error Message");
		httpErros.put("400", "Client Erros");
		httpErros.put("500", "Server Related Errors");

		// EntrySet : returns set view of the Entries in the Map
		System.out.println(httpErros.entrySet());

		for (Map.Entry<String, String> entry : httpErros.entrySet()) {

			String key = entry.getKey();
			System.out.println(entry);
			// System.out.println("values "+httpErros.get(key));

		}

		for (Entry<String, String> entry/* returns an entry */ : httpErros.entrySet()) {
			System.out.println(entry);
		}
	}

}
