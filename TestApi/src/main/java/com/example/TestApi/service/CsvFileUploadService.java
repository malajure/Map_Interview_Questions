package com.example.TestApi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.example.TestApi.config.ApplicationContext;
import com.example.TestApi.dao.UserModeldao;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CsvFileUploadService implements UserModelInterface {

	// logger
	private static Log logger = LogFactory.getLog(CsvFileUploadService.class);

	@Value("${stakeholder.mandatory.username}")
	protected boolean usernameCheck;

	@Value("${stakeholder.mandatory.firstname}")
	protected boolean firstnameCheck;

	@Value("${stakeholder.mandatory.lastname}")
	protected boolean lastnameCheck;

	@Value("${stakeholder.mandatory.gender}")
	protected boolean genderCheck;

	@Value("${stakeholder.mandatory.phone}")
	protected boolean phoneCheck;

	@Value("${stakeholder.mandatory.email}")
	protected boolean emailCheck;

	@Value("${stakeholder.mandatory.company}")
	protected boolean companyCheck;

	@Value("${stakeholder.mandatory.department}")
	protected boolean departmentCheck;

	@Value("${stakeholder.mandatory.designation}")
	protected boolean designationCheck;

	@Value("${stakeholder.mandatory.function}")
	protected boolean functionCheck;

	@Value("${stakeholder.mandatory.subfunction}")
	protected boolean subfunctionCheck;

	@Value("${stakeholder.mandatory.bu}")
	protected boolean buCheck;

	@Value("${stakeholder.mandatory.location}")
	protected boolean locationCheck;

	@Value("${stakeholder.mandatory.band}")
	protected boolean bandCheck;

	@Value("${stakeholder.mandatory.grade}")
	protected boolean gradeCheck;
	////////
	@Value("${user.isupdate.username}")
	protected boolean usernameCheckUser;

	@Value("${user.isupdate.firstname}")
	protected boolean firstnameCheckUser;

	@Value("${user.isupdate.lastname}")
	protected boolean lastnameCheckUser;

	@Value("${user.isupdate.gender}")
	protected boolean genderCheckUser;

	@Value("${user.isupdate.phone}")
	protected boolean phoneCheckUser;

	@Value("${user.isupdate.email}")
	protected boolean emailCheckUser;

	@Value("${user.isupdate.company}")
	protected boolean companyCheckUser;

	@Value("${user.isupdate.department}")
	protected boolean departmentCheckUser;

	@Value("${user.isupdate.designation}")
	protected boolean designationCheckUser;

	@Value("${user.isupdate.function}")
	protected boolean functionCheckUser;

	@Value("${user.isupdate.subfunction}")
	protected boolean subfunctionCheckUser;

	@Value("${user.isupdate.bu}")
	protected boolean buCheckUser;

	@Value("${user.isupdate.location}")
	protected boolean locationCheckUser;

	@Value("${user.isupdate.band}")
	protected boolean bandCheckUser;

	@Value("${user.isupdate.grade}")
	protected boolean gradeCheckUser;

	@Value("${user.isupdate.pref_category}")
	protected boolean prefCategoryCheckUser;

	@Value("${user.isupdate.myWishCompetency}")
	protected boolean myWishCompetencyCheckUser;

	@Value("${user.isupdate.PrefLrnTime}")
	protected boolean PrefLrnTimeCheckUser;

	@Value("${user.isupdate.password}")
	protected boolean passwordCheckUser;

	@Value("${stakeholder.isupdate.username}")
	protected boolean usernameCheckStakeholder;

	@Value("${stakeholder.isupdate.firstname}")
	protected boolean firstnameCheckStakeholder;

	@Value("${stakeholder.isupdate.lastname}")
	protected boolean lastnameCheckStakeholder;

	@Value("${stakeholder.isupdate.gender}")
	protected boolean genderCheckStakeholder;

	@Value("${stakeholder.isupdate.phone}")
	protected boolean phoneCheckStakeholder;

	@Value("${stakeholder.isupdate.email}")
	protected boolean emailCheckStakeholder;

	@Value("${stakeholder.isupdate.company}")
	protected boolean companyCheckStakeholder;

	@Value("${stakeholder.isupdate.department}")
	protected boolean departmentCheckStakeholder;

	@Value("${stakeholder.isupdate.designation}")
	protected boolean designationCheckStakeholder;

	@Value("${stakeholder.isupdate.function}")
	protected boolean functionCheckStakeholder;

	@Value("${stakeholder.isupdate.subfunction}")
	protected boolean subfunctionCheckStakeholder;

	@Value("${stakeholder.isupdate.bu}")
	protected boolean buCheckStakeholder;

	@Value("${stakeholder.isupdate.location}")
	protected boolean locationCheckStakeholder;

	@Value("${stakeholder.isupdate.band}")
	protected boolean bandCheckStakeholder;

	@Value("${stakeholder.isupdate.grade}")
	protected boolean gradeCheckStakeholder;

	@Value("${stakeholder.isupdate.pref_category}")
	protected boolean prefCategoryCheckStakeholder;

	@Value("${stakeholder.isupdate.myWishCompetency}")
	protected boolean myWishCompetencyCheckStakeholder;

	@Value("${stakeholder.isupdate.PrefLrnTime}")
	protected boolean PrefLrnTimeCheckStakeholder;

	@Value("${stakeholder.isupdate.password}")
	protected boolean passwordCheckStakeholder;

	@Value("${department.designation.mapping}")
	protected boolean deptDesgnMap;

	@Value("${function.subFunction.mapping}")
	protected boolean funSubfuncMap;

	@Value("${support.to.emailID}")
	private String supportToEmail;

	@Value("${profileImageFolder}")
	private String profileImageFolder;

	@Value("${moodle_service_URL}")
	protected String moodle_service_URL;
	@Value("#{${bulk_upload_headers}}")
	protected Map<String, String> headermap;

	@Value("#{${bulk_upload_headers_extra}}")
	protected Map<String, String> headermap_extra;

	protected static final String USER_AGENT = "Mozilla/5.0";

	@Autowired
	UserModeldao userModeldao;

	@SuppressWarnings({ "unchecked", "resource" })
	public Map<String, Serializable> performBukVerify(MultipartFile file, String uSentData) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		logger.debug("inside bulkverify2 **********************" + file.getOriginalFilename());

		Map<String, Serializable> props = null;
		Collection<String> allowedKeys = null;
		Collection<String> extra_allowedKeys = null;

		Map<String, String> KeysWithDescription;
		Map<String, String> extraKeysWithDescription;
		Map<String, Serializable> preProcessingMap = new HashMap<String, Serializable>();
		Map<String, Serializable> postProcessingMap = new HashMap<String, Serializable>();
		List<Object> deletedUserList = new ArrayList<Object>();
		List<Object> updatedUserList = new ArrayList<Object>();
		List<Object> addedUserList = new ArrayList<Object>();
		List<Object> failedUserList = new ArrayList<Object>();

		int company_id = 0;
		String company_nm = "";
		String user_id = "";
		String email = "";
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			props = (Map<String, Serializable>) objectMapper.readValue(uSentData, Map.class);
		} catch (Exception e) {
			logger.debug("Error is json to map conversion" + e.getLocalizedMessage());
		}

		// check for the Key wheather it conatins the type as csv or comoanyis with
		// compId
		boolean checkKeyType = props.containsKey("type");

		boolean isCompanyIdExists = false;

		if (!checkKeyType) {
			Serializable company_ser = props.containsKey("company_id") ? props.get("company_id") : 0;

			if (!(company_ser.toString() == null || company_ser.toString().equalsIgnoreCase(""))) {
				if (company_ser instanceof Integer)
					company_id = (int) company_ser;
				else
					company_id = Integer.parseInt((String) company_ser);
				isCompanyIdExists = true;
			}

		}
		logger.debug("company id " + company_id);

		allowedKeys = headermap.values();
		extra_allowedKeys = headermap_extra.values();
		KeysWithDescription = headermap;
		extraKeysWithDescription = headermap_extra;

		logger.debug("KeysWithDescription  are:    " + KeysWithDescription);
		logger.debug("extraKeysWithDescription  are:    " + extraKeysWithDescription);

		logger.debug("KeysWithDescription  are:    " + KeysWithDescription);
		logger.debug("extraKeysWithDescription  are:    " + extraKeysWithDescription);
		Map<String, Serializable> response = new HashMap<String, Serializable>();
		List<Map<String, Serializable>> returnList = new ArrayList();

		try {

			int dataCheckCount = 0;
			// check for user sent type if it is type:csv then it will execute
			if (checkKeyType) {

				logger.debug("in pre processing");
				List<Map<String, Object>> preProcessinglist = new ArrayList();
				if ("csv".equalsIgnoreCase(props.get("type").toString())) {
					CsvPreference prefs = CsvPreference.STANDARD_PREFERENCE;
					CsvListReader csvReader = new CsvListReader(
							new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs), prefs);
					List<String> header = new ArrayList<String>(csvReader.read());// returns the csv Header rows
					Map<String, Object> row = null;
					List<String> eachrow;
					while ((eachrow = csvReader.read()) != null) { // point to teh each row
						row = new HashMap<String, Object>();

						for (int i = 0; i < header.size(); i++) {
							row.put(header.get(i), eachrow.get(i)); // add the header key and row value
						}
						preProcessinglist.add(row); // add the object to the list
					}

				} else {
					preProcessinglist = userModeldao.getAllSapData();
				}
				preProcessingMap.put("rowsData", (Serializable) preProcessinglist);

				Map<String, Serializable> pre_result = userModeldao.preprocessing(preProcessingMap); // truncate
																										// user_staus_creation
																										// table
				if (!pre_result.get("status").toString().equalsIgnoreCase("success")) {
					pre_result.put("reason", pre_result.get("responseMsg"));
					pre_result.put("dataStatus", "failed");
					return pre_result;
				}

			} // end of type:check

			if (checkKeyType && "mysql".equalsIgnoreCase(props.get("type").toString())) {

			} else {
				CsvListReader csvReader = null;
				CsvPreference prefs = CsvPreference.STANDARD_PREFERENCE;
				csvReader = new CsvListReader(
						new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), prefs), prefs);
				List<String> header = new ArrayList<String>(csvReader.read()); // get CSV Header rows
				List<String> eachrow;
				Map<String, Serializable> validate_result;
				while ((eachrow = csvReader.read()) != null) { // point the each row of csv
					try {
						HashMap<String, Serializable> userMap = new HashMap<String, Serializable>();
						List<Map<String, Serializable>> extralist = new ArrayList<Map<String, Serializable>>();
						if (checkKeyType) {
							userMap.put("type", props.get("type"));
						}
						user_id = eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))) != null
								? eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))).toString()
								: "";
						email = eachrow.get(header.indexOf(KeysWithDescription.get("email"))) != null
								? eachrow.get(header.indexOf(KeysWithDescription.get("email"))).toString()
								: "";
						logger.info("user_id " + user_id);
						userModeldao.insertToUserCreationStatus(user_id, "success");
						userModeldao.activateuser(user_id);
						dataCheckCount++;

						userMap.put("isCompanyIdExists", isCompanyIdExists);

						userMap.put("company_id", company_id);

						for (Entry<String, String> entry : KeysWithDescription.entrySet()) {

							if (header.indexOf(entry.getValue()) != -1
									&& eachrow.get(header.indexOf(entry.getValue())) != null) {

								String rowValue = eachrow.get(header.indexOf(entry.getValue()));
								Map<String, String> check_sp_ch_result = checkSpecialCharacters(rowValue);
								if (check_sp_ch_result.get("status").equalsIgnoreCase("failed")) {
									response.put("status", check_sp_ch_result.get("status"));
									response.put("reason", check_sp_ch_result.get("responseMsg"));
									return response;
								} else {
									rowValue = (String) check_sp_ch_result.get("input");
									// add it to userMap
									userMap.put(entry.getKey(), rowValue);
								}

							}
						}

						for (Entry<String, String> entry : extraKeysWithDescription.entrySet()) {
							Map<String, Serializable> extra = new HashMap<String, Serializable>();
							if (header.indexOf(entry.getValue()) != -1
									&& eachrow.get(header.indexOf(entry.getValue())) != null) {
								String rowValue = eachrow.get(header.indexOf(entry.getValue()));
								Map<String, String> check_sp_ch_result = checkSpecialCharacters(rowValue);
								if (check_sp_ch_result.get("status").equalsIgnoreCase("failed")) {
									response.put("status", check_sp_ch_result.get("status"));
									response.put("reason", check_sp_ch_result.get("responseMsg"));
									return response;
								} else {
									rowValue = (String) check_sp_ch_result.get("input");

									extra.put("key", entry.getKey());
									extra.put("value", rowValue);
									extra.put("name", userModeldao.getExtraFieldDescription(entry.getKey()));
								}
							}

							if (header.contains(entry.getValue().toString())) {
								String value = eachrow.get(header.indexOf(entry.getValue()));
								if (value != null && !value.isEmpty() && !"".equalsIgnoreCase(value)) {
									extralist.add(extra);
								} else {
									extra.put("key", entry.getKey());
									extra.put("value", "");
									extra.put("name", userModeldao.getExtraFieldDescription(entry.getKey()));
									extralist.add(extra);
								}
							}

						}

						userMap.put("extra", (Serializable) extralist);
						logger.info("usermap is" + userMap);
						//
						validate_result = validateuser(userMap); /* used for validating given userid from csv */

						if (!validate_result.get("status").toString().equalsIgnoreCase("success")) {
							userMap.put("user_id", user_id);
							userMap.put("firstName",
									validatefstname(userMap).get("firstName") != null
											? validatefstname(userMap).get("firstName")
											: "");
							userMap.put("lastName",
									validatelstname(userMap).get("lastName") != null
											? validatelstname(userMap).get("lastName")
											: "");

						} else {
							userMap.put("user_id", validate_result.get("user_id")); // get the validated userid
						}
						// it will execute when the user_id is va
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatefstname(userMap);
							userMap.put("firstName", validate_result.get("firstName"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatelstname(userMap);
							userMap.put("lastName", validate_result.get("lastName"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validateemail(userMap);
							userMap.put("email", validate_result.get("email"));
							logger.debug(userMap);
						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatecompany(userMap);
							logger.debug(validate_result);
							userMap.put("company_id", validate_result.get("company_id"));
							company_nm = userMap.containsKey("company_txt") ? (String) userMap.get("company_txt") : "";
							logger.debug(userMap);
						}

						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatedept(userMap);
							logger.debug(validate_result);
							userMap.put("dept_id", validate_result.get("dept_id"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validategender(userMap);
							userMap.put("gender", validate_result.get("gender"));
							logger.debug(userMap);

						}

						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validateregion(userMap);
							userMap.put("region_id", validate_result.get("region_id"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatelocation(userMap);
							// validated_Value =validate_result.containsKey("location_id") ? (String)
							// validate_result.get("location_id") : "";
							userMap.put("location_id", validate_result.get("location_id"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validateband(userMap);
							// validated_Value = validate_result.containsKey("band_id") ? (String)
							// validate_result.get("band_id") : "";
							userMap.put("band_id", validate_result.get("band_id"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatebu(userMap);
							// validated_Value = validate_result.containsKey("bu_id") ? (String)
							// validate_result.get("bu_id") : "";
							userMap.put("bu_id", validate_result.get("bu_id"));
							logger.debug(userMap);

						}

						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatedesignation(userMap);
							// validated_Value = validate_result.containsKey("desgn_id") ? (String)
							// validate_result.get("desgn_id") : "";
							userMap.put("desgn_id", validate_result.get("desgn_id"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatefunction(userMap);
							// validated_Value =validate_result.containsKey("func_id") ? (String)
							// validate_result.get("func_id") : "";
							userMap.put("func_id", validate_result.get("func_id"));
							logger.debug(userMap);

						}

						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatesubfunction(userMap);
							// validated_Value = validate_result.containsKey("subfunc_id") ? (String)
							// validate_result.get("subfunc_id") : "";
							userMap.put("subfunc_id", validate_result.get("subfunc_id"));
							logger.debug(userMap);

						}

						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validategrade(userMap);
							// validated_Value =validate_result.containsKey("grade") ? (String)
							// validate_result.get("grade") : "";
							userMap.put("grade", validate_result.get("grade"));
							logger.debug(userMap);

						}
						if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

							validate_result = validatephone(userMap);
							logger.debug(userMap);
							// validated_Value = validate_result.containsKey("phone") ? (String)
							// validate_result.get("phone") : "";
							userMap.put("phone", validate_result.get("phone"));
							logger.debug(userMap);

						}

						if (validate_result.get("status").toString().equalsIgnoreCase("failed")) {
							result = validate_result;

						} else {

							result = verifyAccountV2(userMap, false);

						}
						postProcessingMap.put("company_id", userMap.get("company_id"));

						if (userMap.containsKey("isCompanyIdExists"))
							userMap.remove("isCompanyIdExists");

						if (validate_result.get("status").toString().equalsIgnoreCase("failed")) {
							userMap.put("flag", result.containsKey("flag") ? result.get("flag") : "Add");
						} else {
							userMap.put("flag", result.containsKey("flag") ? result.get("flag") : "null");
						}
						//
						Map<String, Serializable> uMap = new HashMap<String, Serializable>();
						//
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
						logger.debug("usermap:" + userMap);
						uMap.put("userData", (Serializable) userMap);
						uMap.put("reason", result.get("responseMsg"));
						uMap.put("dataStatus", result.get("status"));
						returnList.add(uMap);
						uMap = null;
						result = null;
						userMap = null;

					} catch (Exception exe) {
						logger.error("Expection in bulkverify, " + exe.getMessage());
						exe.printStackTrace();
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
			if (dataCheckCount == 0 && deletedUserList.size() == 0) {
				logger.debug("no count");
				response.put("results", (Serializable) returnList);
				response.put("status", "failed");
				response.put("responseMsg", "no list present");
			} else if (dataCheckCount == 0 && deletedUserList.size() > 0) {
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
			response.put("responseMsg", "Error in adding User");
		}
		if (checkKeyType) {
			response.put("updatedUsers", (Serializable) updatedUserList);
			response.put("addedUsers", (Serializable) addedUserList);
			// response.put("deletedUsers", (Serializable) deletedUserList);
			response.put("failedUsers", (Serializable) failedUserList);
			logger.debug("in post processing in bulkverify ");
			// Sending response to CSV file to trigger an EMAIL
			Map<String, Serializable> postprocessingresult = userModeldao.postprocessing(postProcessingMap);
			company_nm = postprocessingresult != null && postprocessingresult.containsKey("company_nm")
					? (String) postprocessingresult.get("company_nm")
					: company_nm;
			response.put("company_txt", company_nm);
			if (postprocessingresult != null && postprocessingresult.containsKey("status")
					&& !((String) postprocessingresult.get("status")).equalsIgnoreCase("failed")) {
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
				} else
					logger.debug("error in postpreprocessing in bulkverify");

				response.put("deletedUsers", (Serializable) deletedUserList);

			}
			logger.debug("postpreprocessing in bulkverify completed");
			// mail.sendUserSapUpdate(sapemaillist, response);

		}
		return response;

	}

	public Map<String, Serializable> validatedept(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer dept_id = 0;
		String dept = props.containsKey("dept_txt")
				? props.get("dept_txt") != null ? (String) props.get("dept_txt") : ""
				: "";
		dept_id = userModeldao.getDeptIDByName(dept);

		if (departmentCheck) {
			logger.debug("Department mandatory");
			if (dept_id == 0 || dept_id == null) {
				logger.debug("dept_id not provided");
				// result.put("responseMsg", "Please check department");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("departmntcheck");
			} else {
				result.put("dept_id", dept_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Department not mandatory");
			result.put("dept_id", dept_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatecompany(Map<String, Serializable> props) {
		logger.debug("in company" + props);
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		// String type = props.containsKey("type") ? props.get("type").toString() :
		// "".toString();
		// logger.debug("type is" + type);
		Integer company_id = 0;
		if (props.containsKey("company_id")) {

			if (!props.containsKey("type")) {

				if ((props.containsKey("isCompanyIdExists") && !(boolean) props.get("isCompanyIdExists"))) {
					String company_name = props.get("company_txt").toString() != null
							? props.get("company_txt").toString()
							: "";
					company_id = userModeldao.getCompanyIDByName(company_name);
				} else {
					company_id = (Integer) props.get("company_id");
					Map<String, Object> resp = userModeldao.getCompany(company_id);
					if (resp.get("status").toString().equalsIgnoreCase("failed")) {
						return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject
								.get("user")).get("bulk")).get("failed")).get("invalidcompany");

					}
				}

			} else {

				company_id = userModeldao.getCompanyIDByName(props.get("company_txt").toString());

			}
		}

		if (companyCheck) {
			logger.debug("Company mandatory");

			if (company_id == 0 || company_id == null) {
				logger.debug("company not provided");
				// result.put("responseMsg", "Please check company");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("companycheck");
			} else {
				result.put("company_id", company_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Company not Mandatory");
			logger.debug("company value" + company_id);
			result.put("company_id", company_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatesubfunction(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer subfunc_id = 0;
		if (props.containsKey("subfunc_txt")) {
			String value = props.get("subfunc_txt") != null
					? props.get("subfunc_txt").toString().isEmpty() ? (String) props.get("subfunc_txt") : "0"
					: "0";
			subfunc_id = userModeldao.getSubfuncIDByName(value);

		}
		if (subfunctionCheck) {
			logger.debug("SubFunction mandatory");

			if (subfunc_id == 0 || subfunc_id == null) {
				logger.debug("sub_fun_id not provided");
				// result.put("responseMsg", "Please check SubFunction");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("subfunctioncheck");
			} else {
				result.put("subfunc_id", subfunc_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Sub+function  not Mandatory");
			result.put("subfunc_id", subfunc_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatelocation(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer location_id = 0;
		if (props.containsKey("location_txt")) {
			location_id = userModeldao.getLocationByName(props.get("location_txt").toString());

		}
		if (locationCheck) {
			logger.debug("location mandatory");

			if (location_id == 0 || location_id == null) {
				logger.debug("location_id not provided");
				// result.put("responseMsg", "Please check location");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("locationcheck");
			} else {
				result.put("location_id", location_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("location  not Mandatory");
			result.put("location_id", location_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}
	}

	public Map<String, Serializable> validatefunction(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer func_id = 0;
		if (props.containsKey("func_txt")) {

			String value = props.get("func_txt") != null
					? !props.get("func_txt").toString().isEmpty() ? (String) props.get("func_txt") : "0"
					: "0";
			func_id = userModeldao.getFuncIDByName(value);

		}
		if (functionCheck) {
			logger.debug("Function mandatory");

			if (func_id == 0 || func_id == null) {
				logger.debug("fun_id not provided");
				// result.put("responseMsg", "Please check function");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("functioncheck");
			}

			else {
				result.put("func_id", func_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("function  not Mandatory");
			result.put("func_id", func_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}
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

	public Map<String, Serializable> validateregion(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		Integer region = 0;

		if (props.containsKey("region_txt")) {
			String value = props.get("region_txt") != null ? (String) props.get("region_txt") : "0";
			region = Integer.parseInt(props.get("region_txt").toString());
		}
		result.put("region_id", region);
		result.put("status", "success");

		return (Map<String, Serializable>) result;
	}

	public Map<String, Serializable> validatedesignation(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer designation = 0;
		if (props.containsKey("desgn_txt")) {

			designation = userModeldao.getDesgnIDByName(props.get("desgn_txt").toString());

		}

		if (designationCheck) {
			logger.debug("designation mandatory");

			if (designation == 0 || designation == null) {
				logger.debug("desgn_id not provided");
				// result.put("responseMsg", "Please check designation");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("designationcheck");
			} else {
				result.put("desgn_id", designation);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("designation  not Mandatory");
			result.put("desgn_id", designation);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatefstname(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String firstName = props.containsKey("firstName") ? props.get("firstName").toString() : "".toString();
		logger.debug("firstName is" + firstName);
		if (firstnameCheck) {
			logger.debug("First Name Mandatory");

			if (firstName == null || "null".equalsIgnoreCase(firstName) || "".equalsIgnoreCase(firstName)
					|| firstName.toString().isEmpty()) {
				logger.debug("firstName not provided");
				// result.put("responseMsg", "Please check FirstName");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("firstnamecheck");
			} else {

				result.put("status", "success");
				result.put("firstName", firstName);
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("First Name not Mandatory");
			result.put("firstName", firstName);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatelstname(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String lastName = props.containsKey("lastName") ? props.get("lastName").toString() : "".toString();
		logger.debug("lastname is" + lastName);
		if (lastnameCheck) {
			logger.debug("Last Name Mandatory");

			if (lastName == null || "null".equalsIgnoreCase(lastName) || "".equalsIgnoreCase(lastName)
					|| lastName.toString().isEmpty()) {
				logger.debug("lastName not provided");
				// result.put("responseMsg", "Please check Lastname");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("lastnamecheck");
			} else {

				result.put("status", "success");
				result.put("lastName", lastName);
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Last Name not Mandatory");
			result.put("status", "success");
			result.put("lastName", lastName);
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validategrade(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String grade = props.containsKey("grade") ? props.get("grade").toString() : "".toString();
		if (gradeCheck) {
			logger.debug("grade Mandatory");

			if (grade == null || "null".equalsIgnoreCase(grade) || "".equalsIgnoreCase(grade)
					|| grade.toString().isEmpty()) {
				logger.debug("grade not provided");
				// result.put("responseMsg", "Please check grade");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("gradecheck");
			} else {

				result.put("status", "success");
				result.put("grade", grade);
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("grade not Mandatory");
			result.put("status", "success");
			result.put("grade", grade);
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validatebu(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		String bu_id = "";
		String bu = props.containsKey("bu_txt") ? props.get("bu_txt") != null ? (String) props.get("bu_txt") : "" : "";
		bu_id = userModeldao.getBusinessUnitID(bu);

		if (buCheck) {
			logger.debug("business unit Mandatory");

			if (bu_id == null || "".equalsIgnoreCase(bu_id) || bu_id.toString().isEmpty()) {
				logger.debug("bu_id not provided");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("bucheck");
			} else {
				result.put("bu_id", bu_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("business unit not Mandatory");
			result.put("bu_id", bu_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}
	}

	public Map<String, Serializable> validategender(Map<String, Serializable> props) {
		String gender = props.containsKey("gender")
				? ("".equalsIgnoreCase(props.get("gender").toString()) ? "others" : props.get("gender").toString())
				: "others";
		gender = gender.equalsIgnoreCase("M") ? "male" : gender.equalsIgnoreCase("F") ? "female" : gender;
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		if (genderCheck) {
			logger.debug("Gender mandatory");

			if (gender == null || "null".equalsIgnoreCase(gender) || "".equalsIgnoreCase(gender)
					|| gender.toString().isEmpty()) {
				logger.debug("gender not provided");
				// result.put("responseMsg", "Please check gender");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("gendercheck");
			} else {
				result.put("gender", gender);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Gender not mandatory");

			result.put("gender", gender);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}
	}

	public Map<String, Serializable> validatephone(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String phone = props.containsKey("phone") ? props.get("phone").toString() : "".toString();
		if (phoneCheck) {
			logger.debug("Phone mandatory");

			if (phone == null || "null".equalsIgnoreCase(phone) || "".equalsIgnoreCase(phone)
					|| phone.toString().isEmpty() || !phone.matches("^[+-0-9_]*$")) {
				logger.debug("phone");
				// result.put("responseMsg", "Please check phone");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("phonecheck");
			} else {

				result.put("status", "success");
				result.put("phone", phone);
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("phone  not Mandatory");
			result.put("status", "success");
			result.put("phone", phone);
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validateband(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer band_id = 0;
		String regex = "\\d+";

		if (props.containsKey("band_txt")) {
			if (((String) props.get("band_txt")).matches(regex)) {
				band_id = Integer.parseInt((String) props.get("band_txt"));

			} else {
				band_id = userModeldao.getBandIDByName(props.get("band_txt").toString());
			}
		}

		if (bandCheck) {
			logger.debug("Band Mandatory");

			if (band_id == 0 || band_id == null) {
				logger.debug("band not provided");
				// result.put("responseMsg", "Please check band");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("bandcheck");
			} else {
				result.put("band_id", band_id);
				result.put("status", "success");

				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Band  not Mandatory");
			result.put("band_id", band_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

	}

	public Map<String, Serializable> validateuser(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String user_id = props.containsKey("user_id") ? props.get("user_id").toString() : "".toString();
		if (usernameCheck) {
			logger.debug("username Mandatory");

			if (user_id == null || "null".equalsIgnoreCase(user_id) || "".equalsIgnoreCase(user_id)
					|| user_id.toString().isEmpty()) {
				logger.debug("user_id not provided");

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("usernamecheck");
			} else if (!user_id.matches("^[a-zA-Z0-9_]*$")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidUserName");
			} else {
				userModeldao.insertToUserCreationStatus(user_id, "success");
				result.put("status", "success");
				result.put("user_id", user_id);
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("username  not mandatory");
			result.put("user_id", user_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}
	}

	public Map<String, Serializable> verifyAccountV2(Map<String, Serializable> props, boolean flag) {
		logger.debug("inside verifyAccountV2  method ::::::::::::: " + props);

		Map<String, Serializable> result = new HashMap<String, Serializable>();

		String user_id = (String) (props.containsKey("user_id") ? props.get("user_id") : "");
		String email = (String) (props.containsKey("email") ? props.get("email") : "");
		String firstName = (String) (props.containsKey("firstName") ? props.get("firstName") : "");
		String lastName = (String) (props.containsKey("lastName") ? props.get("lastName") : "");
		String grade = props.containsKey("grade") ? props.get("grade").toString() : "".toString();
		// String bu_id=(String) (props.containsKey("bu_id") ? props.get("bu_id") : "");
		String gender = (String) (props.containsKey("gender") ? props.get("gender") : "");
		String phone = (String) (props.containsKey("phone") ? props.get("phone") : "");
		Integer band_id = props.containsKey("band_id")
				&& !(props.get("band_id").toString().isEmpty() || props.get("band_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("band_id").toString())
						: 0;

		String bu_id = props.containsKey("bu_id") ? props.get("bu_id").toString() : "".toString();
		Integer company_id = props.containsKey("company_id") && !(props.get("company_id").toString().isEmpty()
				|| props.get("company_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("company_id").toString())
						: 0;

		Integer designation = props.containsKey("desgn_id") && !(props.get("desgn_id").toString().isEmpty()
				|| props.get("desgn_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("desgn_id").toString())
						: 0;

		Integer location_id = props.containsKey("location_id") && !(props.get("location_id").toString().isEmpty()
				|| props.get("location_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("location_id").toString())
						: 0;

		Integer fun_id = props.containsKey("func_id")
				&& !(props.get("func_id").toString().isEmpty() || props.get("func_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("func_id").toString())
						: 0;

		Integer sub_fun_id = props.containsKey("sub_fun_id") && !(props.get("sub_fun_id").toString().isEmpty()
				|| props.get("sub_fun_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("sub_fun_id").toString())
						: 0;

		Integer region_id = props.containsKey("region_id") && !(props.get("region_id").toString().isEmpty()
				|| props.get("region_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("region_id").toString())
						: 0;

		Integer department = props.containsKey("dept_id")
				&& !(props.get("dept_id").toString().isEmpty() || props.get("dept_id").toString().equalsIgnoreCase(""))
						? Integer.parseInt(props.get("dept_id").toString())
						: 0;
		logger.debug(department);

		logger.debug("in side reg" + email);
		// update scenario through GUI
		boolean emailLogin = true; // Boolean.parseBoolean(ApplicationContext.getGlobalProperty("emailLogin"));
		if (!props.containsKey("type")) {

			// if props contains companyId then it will work
			if (userModeldao.personExists3(user_id)) {
				result.put("responseMsg",
						"The user ID " + user_id + " is existing in the system,so updating with current details");
				if (emailLogin && userModeldao.personExists4(email, user_id)) {
					result.put("responseMsg", "email id already exists to another user_id");
					result.put("status", "failed");
					result.put("flag", "Update");
					return result;
				}
				if (flag == false) {
					result.put("flag", "Update");
					result.put("status", "success");
				} else if (flag == true) {

					result = userModeldao.updateUserData2(user_id, band_id, grade, bu_id, company_id, location_id,
							email, firstName, lastName, phone, gender, department, designation, fun_id, props);
					Map<String, Serializable> MoodleMap = new HashMap<String, Serializable>();
					MoodleMap.put("user_id", user_id);
					MoodleMap.put("email", email);
					MoodleMap.put("firstName", firstName);
					MoodleMap.put("lastName", lastName);
					try {
						updateUser(MoodleMap);
					} catch (NumberFormatException | IOException | JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					logger.debug("updateddddd user------" + result);
					if (result.get("status").toString().equalsIgnoreCase("success")) { // storing into
																						// user_creation_status
						userModeldao.insertToUserCreationStatus(user_id, "success");
						result.put("user_properties", (Serializable) props);
						result.put("responseMsg", "success");
						result.put("status", "success");
						return result;
					} else {
						userModeldao.insertToUserCreationStatus(user_id, "failed");
						result.put("user_properties", (Serializable) props);
						result.put("responseMsg", result.get("responseMsg"));
						result.put("status", "failed");

						return result;
					}

				}

				return result;
			}
		}

		logger.debug("CHK" + email);

		Map<String, Object> verifiedMap = userModeldao.getRegisterAccount2(email);
		String verified = (String) (verifiedMap.containsKey("verified") ? verifiedMap.get("verified") : "");
		logger.debug("verified value of email" + verified);

		logger.debug("reg prop" + props);

		if (department != null && department != 0) {
			Map<String, Object> resp = userModeldao.getDepartment(department);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invaliddepartmentid");
			}
		}

		if (designation != null && designation != 0) {
			Map<String, Object> resp = userModeldao.getDesignation(designation);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invaliddesignationid");
			}
			if (deptDesgnMap) {
				resp = userModeldao.getDeptDesgnMap(department, designation);
				if (resp.get("status").toString().equalsIgnoreCase("failed")) {

					return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
							.get("bulk")).get("failed")).get("invaliddesignationfordept");
				}
			}
		}

		if (fun_id != null && fun_id != 0) {
			Map<String, Object> resp = userModeldao.getFunction(fun_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidfunid");
			}
		}

		if (sub_fun_id != null && sub_fun_id != 0) {
			Map<String, Object> resp = userModeldao.getSubfunction(sub_fun_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidsubfunid");
			}
			if (funSubfuncMap) {
				resp = userModeldao.getfuncSubfuncMap(fun_id, sub_fun_id);
				if (resp.get("status").toString().equalsIgnoreCase("failed")) {

					return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
							.get("bulk")).get("failed")).get("invalidsubfunforfun");
				}
			}
		}

		if (band_id != null && band_id != 0) {
			Map<String, Object> resp = userModeldao.getBand(band_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidband");
			}
		}

		if (bu_id != null && !bu_id.toString().equalsIgnoreCase("")) {
			Map<String, Object> resp = userModeldao.getBu(bu_id.toString());
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidbu_id");
			}
		}

		if (location_id != null && location_id != 0) {
			Map<String, Object> resp = userModeldao.getLocation(location_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidlocation");
			}
		}
		if (company_id != null && company_id != 0) {
			Map<String, Object> resp = userModeldao.getCompany(company_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {

				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("invalidcompany");
			}
		}

		if (emailLogin && verified != null && !"".equalsIgnoreCase(verified) && !"null".equalsIgnoreCase(verified)
				&& !props.containsKey("type")) {
			logger.debug("inside verification of email for inserting users" + verified);
			if (verified.equalsIgnoreCase("true")) {
				result.put("status", "failed");
				result.put("responseMsg", "email_id already exists to another user");
				return result;
			} else {

				result.put("status", "failed");
				result.put("responseMsg", "email_id already exists to another user");
				return result;
			}
		} else {
			logger.debug("inside elseeeeeeeeeeee");

			// if props has a company_id
			if (!props.containsKey("type")) {

				if (flag) {

					String activationToken = getMD5(email + System.currentTimeMillis());

					logger.debug("usernameCheck" + usernameCheck);
					result = userModeldao.addPreregisteredUser2(user_id, band_id, grade, bu_id, company_id, location_id,
							email, firstName, lastName, phone, gender, department, designation, fun_id, sub_fun_id,
							activationToken, "false");
					if (result.get("status").toString().equalsIgnoreCase("success")) { // Temporary

						user_id = result.get("user_id").toString();
						List<Map<String, Object>> extraList = props.containsKey("extra")
								? (List<Map<String, Object>>) props.get("extra")
								: Collections.emptyList();
						if (!extraList.isEmpty() && extraList != null && extraList != Collections.EMPTY_LIST) {
							for (Map<String, Object> map : extraList) {
								if (map.get("value") != null && !"".equalsIgnoreCase((String) map.get("value"))) {
									logger.debug("value is" + map.get("value"));
									userModeldao.insertPreRegisterExtra(user_id, map.get("key").toString(),
											(String) map.get("value"));
								}
							}
						}

						Map<String, Object> userMeta = userModeldao.checkPreregisterData(user_id);
						String login_type = (userMeta.containsKey("login_type") && userMeta.get("login_type") != null)
								? userMeta.get("login_type").toString()
								: "";

						// Base64 base64 = new Base64();
						JSONObject activationTokenMap = new JSONObject();
						try {
							activationTokenMap.put("type", login_type);
							activationTokenMap.put("activationToken", activationToken);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// String encodedActivationToken = new
						// String(base64.encode(activationTokenMap.toString().getBytes()));

						// unauthApi.sendEmail(user_id, activationToken, email, firstName, false);
						String serverUrl = ApplicationContext.getGlobalProperty("serverUrl");
						String url = String.format("%s/#activate?activationtoken=%s", serverUrl, activationToken);
						// async.async_accountActivationEmail("", user_id, url, email, firstName,
						// serverUrl, false);
						result.put("responseMsg",
								"An account activation link has been sent to your registered Email ID(" + email
										+ "). Please follow the link to activate your account.");
						result.put("status", "PreRegistered");
						Map<String, Object> MoodleMap = new HashMap<String, Object>();
						MoodleMap.put("userName", user_id);
						MoodleMap.put("email", email);
						MoodleMap.put("firstName", firstName);
						MoodleMap.put("lastName", lastName);
						try {
							createUser(MoodleMap);
						} catch (IOException | JSONException e) {
							e.printStackTrace();
						}
						userModeldao.insertToUserCreationStatus(user_id, "success");
						result.put("user_properties", (Serializable) props);
						result.put("responseMsg", "success");
						result.put("status", "success");
						return result;

					} else {
						userModeldao.insertToUserCreationStatus(user_id, "failed");
						result.put("responseMsg", "Error in preregistering User");
						result.put("status", "failed");
						result.put("user_properties", (Serializable) props);
						return result;
					}
				} else {
					logger.debug("in else##############");
					result.put("user_properties", (Serializable) props);
					result.put("responseMsg", "success");
					result.put("status", "success");
					result.put("flag", "Add");
					return result;
				}

			} else if (props.containsKey("type")) {
				// if props has type:csv
				props.remove("type");

				result = userModeldao.updateUserData2(user_id, band_id, grade, bu_id, company_id, location_id, email,
						firstName, lastName, phone, gender, department, designation, fun_id, props);
				Map<String, Serializable> MoodleMap = new HashMap<String, Serializable>();
				MoodleMap.put("user_id", user_id);
				MoodleMap.put("email", email);
				MoodleMap.put("firstName", firstName);
				MoodleMap.put("lastName", lastName);
				try {
					updateUser(MoodleMap);
				} catch (NumberFormatException | IOException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				logger.debug("updateddddd user------" + result);
				if (result.get("status").toString().equalsIgnoreCase("success")) { // storing into user_creation_status
					userModeldao.insertToUserCreationStatus(user_id, "success");
					result.put("user_properties", (Serializable) props);
					result.put("responseMsg", "success");
					result.put("status", "success");
					return result;
				} else {
					userModeldao.insertToUserCreationStatus(user_id, "failed");
					result.put("user_properties", (Serializable) props);
					result.put("responseMsg", result.get("responseMsg"));
					result.put("status", "failed");

					return result;
				}

			}
		}
		logger.debug("in verify account" + result);
		return result;
	}

	public void updateUser(Map<String, Serializable> props) throws IOException, NumberFormatException, JSONException {
		logger.debug("im here........props:" + props);
		String email = (String) (props.containsKey("email") ? props.get("email") : "");
		String user_id = (String) (props.containsKey("user_id") ? props.get("user_id") : "");
		if ("".equalsIgnoreCase(user_id) || user_id == null)
			return;
		long id = userModeldao.getMoodleId(user_id);
		logger.debug("moodle id:" + id);
		if (id == 0)
			return;
		String query = "&users[0][id]=" + id;
		if (props.containsKey("firstName"))
			query = query + "&users[0][firstname]=" + props.get("firstName");
		if (props.containsKey("lastName"))
			query = query + "&users[0][lastname]=" + props.get("lastName");
		if (props.containsKey("email"))
			query = query + "&users[0][email]=" + email;
		String request_url = moodle_service_URL + "&wsfunction=core_user_update_users" + query;

		logger.debug("request_url2 : " + request_url);
		URL obj = new URL(request_url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		logger.debug("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			logger.debug("response:" + response.toString());
			if ("null".equalsIgnoreCase(response.toString()))
				logger.debug("it is okkk");
		} else {
			logger.debug("Update request not worked");
		}
	}

	public String getMD5(String input) {
		try {
			logger.debug(input);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			// translate byte to positive(1) biginteger.
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);// from base 16 convert to base 10
			// zero pad it if hashtext.length() < 32 since we want 32 chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void createUser(Map<String, Object> props) throws IOException, JSONException {
		logger.debug("im here........props:" + props);
		String user_Name = ((String) (props.containsKey("userName") ? props.get("userName") : "")).toLowerCase();
		String email = (String) (props.containsKey("email") ? props.get("email") : "");
		Map<String, Serializable> userMap = getUser(user_Name);
		if ((userMap != null && !userMap.isEmpty()) || (user_Name == null || "".equalsIgnoreCase(user_Name)))
			return;
		String password = user_Name.toLowerCase() + "@Cogknit123";
		String firstname = (String) (props.containsKey("firstName") ? props.get("firstName") : "");
		String lastname = (String) (props.containsKey("lastName") ? props.get("lastName") : "");
		lastname = lastname.isEmpty() ? "." : lastname;
		String request_url = moodle_service_URL + "&wsfunction=core_user_create_users&users[0][username]=" + user_Name
				+ "&users[0][password]=" + password + "&users[0][firstname]=" + firstname + "&users[0][lastname]="
				+ lastname + "&users[0][email]=" + email;

		logger.debug("moodle user creation request_url : " + request_url);
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			logger.error("Exception in createUserKarma", e);
		}
		URL obj = null;
		try {
			obj = new URL(request_url);
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException in getUser", e);
		}
		// URL obj = new URL(request_url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		logger.debug("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			logger.debug("str:" + response.toString());
			String response1 = response.toString();
			response1 = response1.replaceAll("\\[", "").replaceAll("\\]", "");
			JSONObject jsonObject = new JSONObject(response1);
			int id = Integer.parseInt(jsonObject.get("id").toString());
			// logger.debug("moodle id:" + id);
			logger.debug("moodle id:" + id);
			if (id == 0)
				logger.debug(user_Name + " moodle user not created");
			else {
				userModeldao.updateMoodleUser(user_Name, id);
				logger.debug(user_Name + " moodle user created successfully");
			}
		} else {
			logger.debug("GET request not worked");
		}
	}

	public Map<String, Serializable> getUser(String uname) {
		try {
			try {
				return getUserImpl(uname);
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				logger.error("the error is : ", e);
			}
		} catch (NumberFormatException e) {
			logger.error("the error is : ", e);
		}
		return Collections.EMPTY_MAP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cogknit.user.parent.service.UserModelInterface#getUserImpl(java.lang.
	 * String)
	 */

	public Map<String, Serializable> getUserImpl(String user_id)
			throws IOException, NumberFormatException, JSONException {
		logger.debug("im here........user_id:" + user_id);
		long id = userModeldao.getMoodleId(user_id);
		if (id == 0)
			return Collections.EMPTY_MAP;
		logger.debug("moodle id:" + id);
		String request_url = moodle_service_URL
				+ "&wsfunction=core_user_get_users&criteria[0][key]=id&criteria[0][value]=" + id;
		// logger.debug("request_url : " + request_url);
		logger.debug("request_url2 : " + request_url);
		URL obj = new URL(request_url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		logger.debug("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			logger.debug("response:" + response.toString());

			String response1 = new String(response);
			JSONObject result = new JSONObject(response1);
			// JSONArray users = jsonObject.getJSONArray("users");
			// logger.debug("it is okkk" + users.getString(0));
			// /////////
			JSONArray arr = result.getJSONArray("users");
			String post_id = arr.getJSONObject(0).getString("id");
			String uname = arr.getJSONObject(0).getString("username");
			String fname = arr.getJSONObject(0).getString("firstname");
			String lname = arr.getJSONObject(0).getString("lastname");
			String fullname = arr.getJSONObject(0).getString("fullname");
			// String email = arr.getJSONObject(0).getString("email");
			Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
			resultMap.put("id", post_id);
			resultMap.put("username", uname);
			resultMap.put("firstname", fname);
			resultMap.put("lastname", lname);
			resultMap.put("fullname", fullname);
			return resultMap;
		} else {
			logger.debug("get request not worked");
			return Collections.EMPTY_MAP;
		}
	}

	public Map<String, Serializable> validateemail(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String email = props.containsKey("email") ? props.get("email").toString() : "".toString();
		if (emailCheck) {
			logger.debug("email Mandatory");

			if (email == null || "null".equalsIgnoreCase(email) || "".equalsIgnoreCase(email)
					|| email.toString().isEmpty()) {
				logger.debug("email not provided");
				// result.put("responseMsg", "Please check Email id");
				// result.put("status", "failed");
				return (Map<String, Serializable>) ((Map) ((Map) ((Map) ApplicationContext.jsonObject.get("user"))
						.get("bulk")).get("failed")).get("emailcheck");
			} else {

				result.put("status", "success");
				result.put("email", email);
				return result;
			}
		} else {
			logger.debug("email  not mandatory");
			result.put("email", email);
			result.put("status", "success");
			return result;
		}
	}

}
