package com.example.TestApi.service;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.example.TestApi.dao.UserModeldao;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserModelService {

	@Value("#{${bulk_upload_headers}}")
	protected Map<String, String> headermap;

	@Value("#{${bulk_upload_headers_extra}}")
	protected Map<String, String> headermap_extra;
	
	@Value("${sapemaillist}")
	protected String sapemaillist;

	private static Log logger = LogFactory.getLog(UserModelService.class);

	@Autowired
	UserModeldao userModeldao;

	// value annoation to get the runtime value
	public List<String> getHeaderFields() {

		Collection<String> allowedKeys = headermap.values();
		Collection<String> extra_allowedKeys = headermap_extra.values();

		List<String> userList = new ArrayList<String>(allowedKeys);
		List<String> userList_extra = new ArrayList<String>(extra_allowedKeys);
		userList.addAll(userList_extra);
		return userList;
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	public Map<String, Serializable> bulkverify(MultipartFile file, String uData) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		logger.debug("inside bulkverify2 **********************" + file.getOriginalFilename());

		Map<String, Serializable> props = null;
		Collection<String> allowedKeys = null;
		Collection<String> extra_allowedKeys = null;

		Map<String, String> KeysWithDescription;
		Map<String, String> extraKeysWithDescription;
		//
		Map<String, Serializable> preProcessingMap = new HashMap<String, Serializable>();
		Map<String, Serializable> postProcessingMap = new HashMap<String, Serializable>();
		//
		List<Object> deletedUserList = new ArrayList<Object>();
		List<Object> updatedUserList = new ArrayList<Object>();
		List<Object> addedUserList = new ArrayList<Object>();
		List<Object> failedUserList = new ArrayList<Object>();

		int company_id = 0;
		String company_nm = "";
		String user_id = "";
		String email = "";

		// serialization and deserialization of Java maps using Jackson
		ObjectMapper mapper = new ObjectMapper();
		// JSON-formatted input string and convert it to a Map<String, String> Java
		// collection:
		try {
			props = (Map<String, Serializable>) mapper.readValue(uData, Map.class);
		} catch (Exception e) {
			logger.error("the error is : ", e);
		}

		// check for
		boolean check = props.containsKey("type");

		// if props map does not contains the type then it will have company key
		if (!check) {
			Serializable company_ser = props.containsKey("company_id") ? (Serializable) props.get("company_id") : 0;
			logger.debug("company_id:" + company_ser);

			if (company_ser instanceof Integer)
				company_id = (int) company_ser;
			else
				company_id = Integer.parseInt((String) company_ser);
		}
		logger.debug("company_id:" + company_id);

		allowedKeys = headermap.values();
		extra_allowedKeys = headermap_extra.values();
		KeysWithDescription = headermap;
		extraKeysWithDescription = headermap_extra;

		logger.debug("KeysWithDescription  are:    " + KeysWithDescription);
		logger.debug("extraKeysWithDescription  are:    " + extraKeysWithDescription);

		Map<String, Serializable> response = new HashMap<String, Serializable>();
		List<Map<String, Serializable>> returnList = new ArrayList<Map<String, Serializable>>(1);
		try {
			int dataCheckCount = 0;
			// pre-processing
			if (check) {
				logger.debug("in pre processing");
				List<Map<String, Object>> preProcessinglist = new ArrayList<Map<String, Object>>();
				if ("csv".equalsIgnoreCase(props.get("type").toString())) {
					CsvPreference prefs = new CsvPreference.Builder('"', '|', "\r\n").ignoreEmptyLines(true).build();
					CsvListReader csvReader = new CsvListReader(
							new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs), prefs);
					List<String> header = new ArrayList<String>(csvReader.read());
					if (header.get(0) != null && header.get(0).contains(",")) {
						prefs = CsvPreference.STANDARD_PREFERENCE;
						csvReader = new CsvListReader(
								new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs),
								prefs);
						header = new ArrayList<String>(csvReader.read());
						logger.debug("comma seperator");

					}

					Map<String, Object> row = null;
					List<String> eachrow;
					while ((eachrow = csvReader.read()) != null) {
						row = new HashMap<String, Object>();

						for (int i = 0; i < header.size(); i++) {
							if (header.get(i) != null && eachrow.get(i) !=null) {
								row.put(header.get(i), eachrow.get(i));
							}
						}
						preProcessinglist.add(row);
					}

				} else {
					preProcessinglist = userModeldao.getAllSapData();
				}
				preProcessingMap.put("rowsData", (Serializable) preProcessinglist);
				Map<String, Serializable> pre_result = userModeldao.preprocessing(preProcessingMap);
				if (!pre_result.get("status").toString().equalsIgnoreCase("success")) {
					pre_result.put("reason", pre_result.get("responseMsg"));
					pre_result.put("dataStatus", "failed");
					return pre_result;
				}
			}
			
			if (check && "mysql".equalsIgnoreCase(props.get("type").toString())) 
			{
				

			}

			else {
				CsvPreference prefs = new CsvPreference.Builder('"', '|', "\r\n").ignoreEmptyLines(true).build();
				CsvListReader csvReader = new CsvListReader(
						new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs), prefs);
				List<String> header = new ArrayList<String>(csvReader.read());
				if(header.get(0)!=null&&header.get(0).contains(","))
				{
					prefs = CsvPreference.STANDARD_PREFERENCE;
					  csvReader = new CsvListReader(
								new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs), prefs);
						header = new ArrayList<String>(csvReader.read());
					 logger.debug("comma seperator");
					 
				}
				List<String> eachrow;
				Map<String, Serializable> validate_result;
				while ((eachrow = csvReader.read()) != null) {
					try {

					HashMap<String, Serializable> userMap = new HashMap<String, Serializable>();
					List<Map<String, Serializable>> extralist = new ArrayList<Map<String, Serializable>>();
					if (check) {
						userMap.put("type", props.get("type"));
					}
					userModeldao.activateuser(user_id);
					dataCheckCount++;
					user_id = eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))) != null
							? eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))).toString()
							: "";
					email = eachrow.get(header.indexOf(KeysWithDescription.get("email"))) != null
									? eachrow.get(header.indexOf(KeysWithDescription.get("email"))).toString()
									: "";
					logger.info("user_id " + user_id);
					userModeldao.insertToUserCreationStatus(user_id, "success");
					
					if (eachrow.get(header.indexOf(KeysWithDescription.get("Status_Code"))) == null
							|| eachrow.get(header.indexOf(KeysWithDescription.get("Status_Code"))).equals("")) {
						logger.debug("in failed condition of status code");
						Map<String, Serializable> uMap = new HashMap<String, Serializable>();
						uMap.put("reason", "Status Code not Provided");
						uMap.put("dataStatus", "failed");
						userMap.put("user_id", user_id);
						userMap.put("email", email);
						userMap.put("firstName",
								eachrow.get(header.indexOf(KeysWithDescription.get("firstName"))) != null
										? eachrow.get(header.indexOf(KeysWithDescription.get("firstName")))
										: "");
						userMap.put("lastName",
								eachrow.get(header.indexOf(KeysWithDescription.get("lastName"))) != null
										? eachrow.get(header.indexOf(KeysWithDescription.get("lastName")))
										: "");
						uMap.put("userData", (Serializable) userMap);
						failedUserList.add(uMap);
						returnList.add(uMap);
						continue;
					
					} else {
						String Status_Code = eachrow.get(header.indexOf(KeysWithDescription.get("Status_Code")));
						int statusCode = Status_Code.equalsIgnoreCase("A") ? 1 : 0;
						logger.debug(" statusCode  " + statusCode );
						if (statusCode == 0 ) {
							logger.debug("in suspension");
							Map<String, Serializable> uMap = new HashMap<String, Serializable>();
							uMap.put("reason", "Inactive User");
							uMap.put("dataStatus", "success");
							userMap.put("user_id", user_id);
							userMap.put("email", email);
							userMap.put("firstName",
									eachrow.get(header.indexOf(KeysWithDescription.get("firstName"))) != null
											? eachrow.get(header.indexOf(KeysWithDescription.get("firstName")))
											: "");
							userMap.put("lastName",
									eachrow.get(header.indexOf(KeysWithDescription.get("lastName"))) != null
											? eachrow.get(header.indexOf(KeysWithDescription.get("lastName")))
											: "");
							userModeldao.deactivateuser(user_id);
							userMap.put("flag", "Deleted");
							uMap.put("userData", (Serializable) userMap);
							deletedUserList.add(uMap);
							returnList.add(uMap);
							continue;

						}
					}

					userMap.put("company_id", company_id);
					for (Entry<String, String> entry : KeysWithDescription.entrySet()) {
						if (header.indexOf(entry.getValue()) != -1 && eachrow.get(header.indexOf(entry.getValue())) != null) {
							logger.debug(entry.getKey() + "  " + eachrow.get(header.indexOf(entry.getValue())) + " "
									+ header.indexOf(entry.getValue()));
							String value = eachrow.get(header.indexOf(entry.getValue()));
							Map<String, String> check_sp_charcater = checkSpecialCharacters(value);
							if (check_sp_charcater.get("status").toString().equalsIgnoreCase("failed")) {
								response.put("status", "failed");
								response.put("responseMsg", check_sp_charcater.get("responseMsg"));
								return response;
							} else
								value = (String) check_sp_charcater.get("input");
							userMap.put(entry.getKey(), value);
						}
					}

					// for extra keys
					for (Entry<String, String> entry : extraKeysWithDescription.entrySet()) {
						Map<String, Serializable> extra = new HashMap<String, Serializable>();
						if (header.indexOf(entry.getValue()) != -1 && eachrow.get(header.indexOf(entry.getValue())) != null) {
							logger.debug(entry.getKey() + "  " + eachrow.get(header.indexOf(entry.getValue())) + " "
									+ header.indexOf(entry.getValue()));
							String value = eachrow.get(header.indexOf(entry.getValue()));
							Map<String, String> check_sp_charcater = checkSpecialCharacters(value);
							if (check_sp_charcater.get("status").toString().equalsIgnoreCase("failed")) {
								response.put("status", "failed");
								response.put("responseMsg", check_sp_charcater.get("responseMsg"));
								return response;
							} else
								value = (String) check_sp_charcater.get("input");
							extra.put("key", entry.getKey());
							extra.put("value", value);
							extra.put("name", userModeldao.getExtraFieldDescription(entry.getKey()));
						}

						extralist.add(extra);

					}

					userMap.put("extra", (Serializable) extralist);
					//logger.debug("usermap is" + userMap);
					validate_result =new HashMap<>();
					userMap.put("user_id", validate_result.get("user_id"));
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatefstname(userMap);
						logger.debug(validate_result.get("firstName"));
						userMap.put("firstName", validate_result.get("firstName"));
						

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatelstname(userMap);
						logger.debug(validate_result.get("lastName"));
						userMap.put("lastName", validate_result.get("lastName"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validateemail(userMap);
						logger.debug(validate_result.get("email"));
						userMap.put("email", validate_result.get("email"));
					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatecompany(userMap);
						logger.debug(validate_result);
						userMap.put("company_id", validate_result.get("company_id"));
						//company_nm=userMap.containsKey("company_txt")?(String) userMap.get("company_txt"):"";
					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatedept(userMap);
						logger.debug(validate_result);
						userMap.put("dept_id", validate_result.get("dept_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validategender(userMap);
						userMap.put("gender", validate_result.get("gender"));
						

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validateregion(userMap);
						userMap.put("region_id", validate_result.get("region_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatelocation(userMap);
						// validated_Value =validate_result.containsKey("location_id") ? (String)
						// validate_result.get("location_id") : "";
						userMap.put("location_id", validate_result.get("location_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

					//	validate_result = validateband(userMap);
						// validated_Value = validate_result.containsKey("band_id") ? (String)
						// validate_result.get("band_id") : "";
						userMap.put("band_id", validate_result.get("band_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatebu(userMap);
						// validated_Value = validate_result.containsKey("bu_id") ? (String)
						// validate_result.get("bu_id") : "";
						userMap.put("bu_id", validate_result.get("bu_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						//validate_result = validatedesignation(userMap);
						// validated_Value = validate_result.containsKey("desgn_id") ? (String)
						// validate_result.get("desgn_id") : "";
						userMap.put("desgn_id", validate_result.get("desgn_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

					//	validate_result = validatefunction(userMap);
						// validated_Value =validate_result.containsKey("func_id") ? (String)
						// validate_result.get("func_id") : "";
						userMap.put("func_id", validate_result.get("func_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

					//	validate_result = validatesubfunction(userMap);
						// validated_Value = validate_result.containsKey("subfunc_id") ? (String)
						// validate_result.get("subfunc_id") : "";
						userMap.put("subfunc_id", validate_result.get("subfunc_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

					//	validate_result = validategrade(userMap);
						// validated_Value =validate_result.containsKey("grade") ? (String)
						// validate_result.get("grade") : "";
						userMap.put("grade", validate_result.get("grade"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

					//	validate_result = validatephone(userMap);
						//logger.debug(userMap);
						// validated_Value = validate_result.containsKey("phone") ? (String)
						// validate_result.get("phone") : "";
						userMap.put("phone", validate_result.get("phone"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("failed")) {
						result = validate_result;

					} else {

						//result = verifyAccountV2(userMap, false);

					}
					postProcessingMap.put("company_id", userMap.get("company_id"));
					userMap.put("flag", result.containsKey("flag")?result.get("flag"):"null");
					Map<String, Serializable> uMap = new HashMap<String, Serializable>();
					if (result.containsKey("status") && result.get("status").toString().equals("failed")) {

						uMap.put("reason", result.get("responseMsg"));
						failedUserList.add(uMap);
					} else if (result.containsKey("flag") && result.get("flag").toString().equals("created")) {

						uMap.put("reason", result.get("responseMsg"));
						addedUserList.add(uMap);
					} else if (result.containsKey("flag") && result.get("flag").toString().equals("updated")) {

						uMap.put("reason", result.get("responseMsg"));
						updatedUserList.add(uMap);
					} else {

						uMap.put("reason", result.get("responseMsg"));
						addedUserList.add(uMap);
					}

					logger.debug("response construction for user:" + result);
					uMap.put("userData", (Serializable) userMap);
					uMap.put("reason", result.get("responseMsg"));
					uMap.put("dataStatus", result.get("status"));
					returnList.add(uMap);
					uMap = null;
					result = null;
					userMap = null;
				}
				catch (Exception exe) 
					{
					logger.error("Expection in bulkverify, "+exe.getMessage());
					Map<String, Serializable> uMap = new HashMap<String, Serializable>();
					uMap.put("reason", "Error in adding User");
					uMap.put("dataStatus", "failed");
					HashMap userMap = new HashMap<String, Serializable>();
					uMap.put("dataStatus", "failed");
					userMap.put("user_id", user_id);
					userMap.put("email", email);
					userMap.put("firstName",
							eachrow.get(header.indexOf(KeysWithDescription.get("firstName"))) != null
									? eachrow.get(header.indexOf(KeysWithDescription.get("firstName")))
									: "");
					userMap.put("lastName",
							eachrow.get(header.indexOf(KeysWithDescription.get("lastName"))) != null
									? eachrow.get(header.indexOf(KeysWithDescription.get("lastName")))
									: "");
					uMap.put("userData", (Serializable) userMap);
					failedUserList.add(uMap);
					returnList.add(uMap);
					continue;
					
				}
				}
				csvReader.close();
			
			}
			if (dataCheckCount == 0 && deletedUserList.size() == 0) 
			{
				logger.debug("no count");
				response.put("results", (Serializable) returnList);
				response.put("status", "failed");
				response.put("responseMsg", "no list present");
			} else if (dataCheckCount == 0 && deletedUserList.size() > 0) 
			{
				logger.debug("no count");
				response.put("results", (Serializable) returnList);
				response.put("status", "failed");
				response.put("responseMsg", " users in the list are deleted");
			}

			response.put("results", (Serializable) returnList);
			response.put("status", "success");
			response.put("responseMsg", "files uploaded successfully");
			
			

		} catch (Exception e) {
			response.put("results", (Serializable) Collections.EMPTY_LIST);
			response.put("status", "failed");
			response.put("responseMsg", e.getMessage());
		}
		
		if (check) {
			response.put("company_txt", "L&T");
			response.put("updatedUsers", (Serializable) updatedUserList);
			response.put("addedUsers", (Serializable) addedUserList);
			response.put("deletedUsers", (Serializable) deletedUserList);
			response.put("failedUsers", (Serializable) failedUserList);
			logger.debug("in post processing in bulkverify ");
			// Sending response to CSV file to trigger an EMAIL
			Map<String, Serializable> postprocessingresult = userModeldao.postprocessing(postProcessingMap);
			if (postprocessingresult!=null&&!((String) postprocessingresult.get("status")).equalsIgnoreCase("failed")) {
				if (postprocessingresult.containsKey("deletedpreregisterusers")) {
					for (Map<String, Serializable> map : (List<Map<String, Serializable>>) postprocessingresult
							.get("deletedpreregisterusers")) {

						Map<String, Object> tempmap = new HashMap<String, Object>();
						tempmap.put("userData", map);
						tempmap.put("reason", "deleted");
						deletedUserList.add(tempmap);

					}
				}
				if (postprocessingresult.containsKey("suspendedUsers")) {
					for (Map<String, Serializable> map : (List<Map<String, Serializable>>) postprocessingresult
							.get("suspendedUsers")) {

						Map<String, Object> tempmap = new HashMap<String, Object>();
						tempmap.put("userData", map);
						tempmap.put("reason", "suspended");
						deletedUserList.add(tempmap);

					}
				}else
					logger.debug("error in postpreprocessing in bulkverify");

				response.put("deletedUsers", (Serializable) deletedUserList);
				
			} 
			logger.debug("postpreprocessing in bulkverify completed");
			//mail.sendUserSapUpdate(sapemaillist, response);
			
		}


		return response;

	}
	
	
	public Map<String, String> checkSpecialCharacters(String value) {
		Map<String, String> result = new HashMap<String, String>();
		value = value != null ? value.trim() : "";
		value = value.replaceAll("[\\r\\n]", "");
		Pattern pt = Pattern.compile("[^a-zA-Z0-9-\\S.\\/<>?;:,\"'`!@#$%^&*()\\[\\]{}_+=|\\\\~ ]");
		if (!"".equalsIgnoreCase(value) && !"null".equalsIgnoreCase(value)) {

			Matcher match = pt.matcher(value);
			if (match.find()) {
				result.put("status", "failed");
				result.put("responseMsg", "Please check the column value of " + value);
				return result;
			}
		}

		result.put("status", "success");
		result.put("input", value);
		return result;

	}


}
