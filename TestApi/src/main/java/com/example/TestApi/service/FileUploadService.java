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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.example.TestApi.dao.UserModeldao;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileUploadService {

	private static Log logger = LogFactory.getLog(FileUploadService.class);
	@Autowired
	UserModeldao userModeldao;

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


	

	@Value("#{${bulk_upload_headers}}")
	Map<String, String> headermap;

	@Value("#{${bulk_upload_headers_extra}}")
	Map<String, String> headermap_extra;

	public Map<String, Serializable> doBulkVerify(MultipartFile file, String uData) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		logger.debug("inside bulkverify2 **********************" + file.getOriginalFilename());
		Map<String, Serializable> props = null;
		Collection<String> allowedKeys = null;
		Collection<String> extra_allowedKeys = null;
		//
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
		//
		int company_id = 0;
		String company_nm = "";
		String user_id = "";
		String email = "";
		//
		ObjectMapper mapper = new ObjectMapper();

		try {
			props = (Map<String, Serializable>) mapper.readValue(uData, Map.class);
		} catch (Exception e) {
			logger.error("the error is : ", e);
		}

		// check for Json Type
		boolean requestType = props.containsKey("type");

		if (!requestType) {

			Serializable company_ser_id = props.containsKey("company_id") ? props.get("company_id") : 0;
			logger.debug("company_id" + company_id);

			if (company_ser_id instanceof Integer) {
				company_id = (int) company_ser_id;
			} else {
				company_id = Integer.parseInt((String) company_ser_id);
			}
		}

		logger.debug("company_id" + company_id);

		// get csv header values
		allowedKeys = headermap.values();

		// get the csv extra header values
		extra_allowedKeys = headermap_extra.values();

		// extra column keys and rows in csv
		KeysWithDescription = headermap;
		extraKeysWithDescription = headermap_extra;

		logger.debug("KeysWithDescription  are:    " + KeysWithDescription);
		logger.debug("extraKeysWithDescription  are:    " + extraKeysWithDescription);
		//
		Map<String, Serializable> response = new HashMap<String, Serializable>();
		List<Map<String, Serializable>> returnList = new ArrayList<Map<String, Serializable>>();

		try {

			int dataCheckCount = 0;
			if (requestType) {
				logger.debug("in pre processing");
				List<Map<String, Object>> preProcessinglist = new ArrayList<Map<String, Object>>();
				if ("csv".equalsIgnoreCase(props.get("type").toString())) {

					// Before reading or writing csv set the delimiter related config
					CsvPreference delimeterPref = new CsvPreference.Builder('"', '|', "\n").build();
					// Quote char ="
					// ENd of line syb = \n
					// pipe deli = |
					// Some CSV files don’t conform to RFC4180 and have a different number of
					// columns on each row.
					// as you do not know the number of columns in any row. in CSV FIle for this USE
					// --- >
					// So you read all columns in a row in a List and then based on size of list,
					// you determine how you may want to handle the read values.

					ICsvListReader csvReader = new CsvListReader(
							new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), delimeterPref),
							delimeterPref);

					List<String> headerRows = csvReader.read();
					// Entire header row as one Sring element
					if (headerRows.get(0) != null && headerRows.get(0).contains(",")) {
						delimeterPref = CsvPreference.STANDARD_PREFERENCE;
						csvReader = new CsvListReader(new SkipBlankLinesTokenizer(
								new InputStreamReader(file.getInputStream()), delimeterPref), delimeterPref);
						headerRows = new ArrayList<String>(csvReader.read());
						logger.debug("comma seperator");
						logger.info("size " + headerRows.size());
					}

					Map<String, Object> row = null;
					List<String> everyRow;

					while ((everyRow = csvReader.read()) != null) {
						// list of header keys are not null
						row = new HashMap<String, Object>();

						for (int i = 0; i < headerRows.size(); i++) {
							row.put(headerRows.get(i), everyRow.get(i));
						}
						preProcessinglist.add(row);
					}

				} else {
					// Get the processing data from the db service
					preProcessinglist = userModeldao.getAllSapData();
				}

				// add preprocessinglist to map
				preProcessingMap.put("rowData", (Serializable) preProcessinglist);

				// send this to user_creation_status table : rowDat to dn
				Map<String, Serializable> preProcessedDbResult = userModeldao.preprocessing(preProcessingMap);
				// truncate user_creation_status table
				if (!preProcessedDbResult.get("status").toString().equalsIgnoreCase("success")) {
					preProcessedDbResult.put("dataStatus", preProcessedDbResult.get("status"));
					preProcessedDbResult.put("reason", preProcessedDbResult.get("responseMsg"));
					return preProcessedDbResult;

				}

			}
			// for Mysql Type
			if (requestType && "mysql".equalsIgnoreCase(props.get("type").toString())) {
				List<Map<String, Object>> sap_data2 = new ArrayList<Map<String, Object>>();
				// sap_data table is from lafarge db
				sap_data2 = userModeldao.getAllSapData();

				// itarte each

			} else {

				CsvListReader csvListReader = null;
				CsvPreference preference = CsvPreference.STANDARD_PREFERENCE;
				csvListReader = new CsvListReader(
						new SkipBlankLinesTokenizer(new InputStreamReader(file.getInputStream()), preference),
						preference);
				List<String> header = new ArrayList<String>(csvListReader.read());

				List<String> eachrow;
				Map<String, Serializable> validate_result;
				Map<String, Serializable> row = null;
				while ((eachrow = csvListReader.read()) != null) {

					
					try {
					HashMap<String, Serializable> userMap = new HashMap<String, Serializable>();
					List<Map<String, Serializable>> extralist = new ArrayList<Map<String, Serializable>>();

					if (requestType) {
						userMap.put("type", props.get("type"));
					}
					user_id = eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))) != null
							? eachrow.get(header.indexOf(KeysWithDescription.get("user_id"))).toString()
							: "";
					email = eachrow.get(header.indexOf(KeysWithDescription.get("email"))) != null
							? eachrow.get(header.indexOf(KeysWithDescription.get("email"))).toString()
							: "";

					logger.info("user_id " + user_id);

					// pass each user row object to ; inser user_id and success as a status in db
					// tab user_creation_status
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
					} else if (eachrow.get(header.indexOf(extraKeysWithDescription.get("Leaving_Date"))) == null
							|| eachrow.get(header.indexOf(extraKeysWithDescription.get("Leaving_Date"))).equals("")) {

						logger.debug("in failed condition of Leaving date");

						Map<String, Serializable> uMap = new HashMap<String, Serializable>();
						uMap.put("reason", "Leaving date not Provided");
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
						String leavingDate = eachrow.get(header.indexOf(extraKeysWithDescription.get("Leaving_Date")));
						int statusCode = Status_Code.equalsIgnoreCase("p") ? 1 : Integer.parseInt(Status_Code);
						logger.debug("leavingDate and statusCode  " + statusCode + ";" + leavingDate);
						if (statusCode == 0 && !leavingDate.equalsIgnoreCase("1800-01-01")) {
							logger.debug("in suspension");
							Map<String, Serializable> uMap = new HashMap<String, Serializable>();
							uMap.put("reason", "success");
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
							// delete from pre-register and update in user_data as suspended
							userModeldao.deactivateuser(user_id);
							userMap.put("flag", "Deleted");
							uMap.put("userData", (Serializable) userMap);
							deletedUserList.add(uMap);
							returnList.add(uMap);
							continue;

						}
					}
					// activate the user_id to activated from the previous step
					userModeldao.activateuser(user_id);
					dataCheckCount++;
					userMap.put("company_id", company_id);

					// for KeysWithDesc
					for (Entry<String, String> entry : KeysWithDescription.entrySet()) {
						if (header.indexOf(entry.getValue()) != -1
								&& eachrow.get(header.indexOf(entry.getValue())) != null) {
							logger.debug(entry.getKey() + "  " + eachrow.get(header.indexOf(entry.getValue())) + " "
									+ header.indexOf(entry.getValue()));
							userMap.put(entry.getKey(), eachrow.get(header.indexOf(entry.getValue())));
						}
					}
					// for extra keys
					for (Entry<String, String> entry : extraKeysWithDescription.entrySet()) {
						Map<String, Serializable> extra = new HashMap<String, Serializable>();
						if (header.indexOf(entry.getValue()) != -1
								&& eachrow.get(header.indexOf(entry.getValue())) != null) {
							logger.debug(entry.getKey() + "  " + eachrow.get(header.indexOf(entry.getValue())) + " "
									+ header.indexOf(entry.getValue()));
							extra.put("key", entry.getKey());
							extra.put("value", eachrow.get(header.indexOf(entry.getValue())));
							extra.put("name", userModeldao.getExtraFieldDescription(entry.getKey()));
						}

						extralist.add(extra);

					}
					userMap.put("extra", (Serializable) extralist);
					// logger.debug("usermap is" + userMap);
					validate_result = validateuser(userMap);
					userMap.put("user_id", validate_result.get("user_id"));
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatefstname(userMap);
						logger.debug(validate_result.get("firstName"));
						userMap.put("firstName", validate_result.get("firstName"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatelstname(userMap);
						logger.debug(validate_result.get("lastName"));
						userMap.put("lastName", validate_result.get("lastName"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validateemail(userMap);
						logger.debug(validate_result.get("email"));
						userMap.put("email", validate_result.get("email"));
					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatecompany(userMap);
						logger.debug(validate_result);
						userMap.put("company_id", validate_result.get("company_id"));
						// company_nm=userMap.containsKey("company_txt")?(String)
						// userMap.get("company_txt"):"";
					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatedept(userMap);
						logger.debug(validate_result);
						userMap.put("dept_id", validate_result.get("dept_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validategender(userMap);
						userMap.put("gender", validate_result.get("gender"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validateregion(userMap);
						userMap.put("region_id", validate_result.get("region_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatelocation(userMap);
						// validated_Value =validate_result.containsKey("location_id") ? (String)
						// validate_result.get("location_id") : "";
						userMap.put("location_id", validate_result.get("location_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validateband(userMap);
						// validated_Value = validate_result.containsKey("band_id") ? (String)
						// validate_result.get("band_id") : "";
						userMap.put("band_id", validate_result.get("band_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatebu(userMap);
						// validated_Value = validate_result.containsKey("bu_id") ? (String)
						// validate_result.get("bu_id") : "";
						userMap.put("bu_id", validate_result.get("bu_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatedesignation(userMap);
						// validated_Value = validate_result.containsKey("desgn_id") ? (String)
						// validate_result.get("desgn_id") : "";
						userMap.put("desgn_id", validate_result.get("desgn_id"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatefunction(userMap);
						// validated_Value =validate_result.containsKey("func_id") ? (String)
						// validate_result.get("func_id") : "";
						userMap.put("func_id", validate_result.get("func_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatesubfunction(userMap);
						// validated_Value = validate_result.containsKey("subfunc_id") ? (String)
						// validate_result.get("subfunc_id") : "";
						userMap.put("subfunc_id", validate_result.get("subfunc_id"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validategrade(userMap);
						// validated_Value =validate_result.containsKey("grade") ? (String)
						// validate_result.get("grade") : "";
						userMap.put("grade", validate_result.get("grade"));

					}
					if (validate_result.get("status").toString().equalsIgnoreCase("success")) {

						validate_result = validatephone(userMap);
						// logger.debug(userMap);
						// validated_Value = validate_result.containsKey("phone") ? (String)
						// validate_result.get("phone") : "";
						userMap.put("phone", validate_result.get("phone"));

					}

					if (validate_result.get("status").toString().equalsIgnoreCase("failed")) {
						result = validate_result;

					} else {

						// result = verifyAccountV2(userMap, false);

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
					catch (Exception exe) {
						logger.error("Expection in bulkverify, "+exe.getMessage());
						Map<String, Serializable> uMap = new HashMap<String, Serializable>();
						uMap.put("reason", "Error in adding User");
						uMap.put("dataStatus", "failed");
						HashMap userMap = new HashMap<String, Serializable>();
						userMap.put("user_id", user_id);
						userMap.put("email", email);
						uMap.put("userData", (Serializable) userMap);
						failedUserList.add(uMap);
						returnList.add(uMap);
						continue;
						
					}
				}
				csvListReader.close();
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

			

		} catch (Exception e) {
			response.put("results", (Serializable) Collections.EMPTY_LIST);
			response.put("status", "failed");
			return response;
		
			// response.put("responseMsg", e.getMessage());
		}
		response.put("results", (Serializable) returnList);
		response.put("status", "success");
		response.put("responseMsg", "files uploaded successfully");
		//logger.debug("response  " + response);
		// condition to delete the users not in sap_file
		
		//if type is in uData json object true then used for BackEnd Processing
		if (requestType) 
		{
			response.put("company_txt", company_nm);
			response.put("updatedUsers", (Serializable) updatedUserList);
			response.put("addedUsers", (Serializable) addedUserList);
			response.put("deletedUsers", (Serializable) deletedUserList);
			response.put("failedUsers", (Serializable) failedUserList);
			logger.debug("in post processing in bulkverify ");
			
			// Sending response to CSV file to trigger an EMAIL
			Map<String, Serializable> postprocessingresult = userModeldao.postprocessing(postProcessingMap);
		
			if (!((String) postprocessingresult.get("status")).equalsIgnoreCase("failed")&& postprocessingresult
					.containsKey("suspendedUsers")&& postprocessingresult
					.containsKey("deletedpreregisterusers")) {
				// response.put("deletedPreregisteredUsers",
				// (Serializable) postprocessingresult.get("deletedpreregisterusers"));
				for (Map<String, Serializable> map : (List<Map<String, Serializable>>) postprocessingresult
						.get("deletedpreregisterusers")) {

					Map<String, Object> tempmap = new HashMap<String, Object>();
					tempmap.put("userData", map);
					tempmap.put("reason", "deleted");
					deletedUserList.add(tempmap);

				}
				for (Map<String, Serializable> map : (List<Map<String, Serializable>>) postprocessingresult
						.get("suspendedUsers")) {

					Map<String, Object> tempmap = new HashMap<String, Object>();
					tempmap.put("userData", map);
					tempmap.put("reason", "suspended");
					deletedUserList.add(tempmap);

				}
				
				response.put("deletedUsers", (Serializable) deletedUserList);
				//mail.sendUserSapUpdate(sapemaillist, response);
			} else
				logger.debug("error in postpreprocessing in bulkverify");
		}
		return response;

	}

	
	/* Validai=tion Results */

	public Map<String, Serializable> validateuser(Map<String, Serializable> props) {

		Map<String, Serializable> result = new HashMap<String, Serializable>();
		String user_id = props.containsKey("user_id") ? props.get("user_id").toString() : "".toString();
		if (usernameCheck) {
			logger.debug("username Mandatory");

			if (user_id == null || "null".equalsIgnoreCase(user_id) || "".equalsIgnoreCase(user_id)
					|| user_id.toString().isEmpty()) {
				logger.debug("user_id not provided");

				return null;
			} else if (!user_id.matches("^[a-zA-Z0-9_]*$")) {

				return null;
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

	@SuppressWarnings("unchecked")
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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
				return null;
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

	public Map<String, Serializable> validatecompany(Map<String, Serializable> props) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();

		Integer company_id = 0;
		if (props.containsKey("company_id")) {

			company_id = Integer.parseInt(props.get("company_id").toString());

			Map<String, Object> resp = userModeldao.getCompany(company_id);
			if (resp.get("status").toString().equalsIgnoreCase("failed")) {
				return null;

			}
		}
		if (companyCheck) {
			logger.debug("Company mandatory");

			if (company_id == 0 || company_id == null) {
				logger.debug("company not provided");
				// result.put("responseMsg", "Please check company");
				// result.put("status", "failed");
				return null;
			} else {
				result.put("company_id", company_id);
				result.put("status", "success");
				return (Map<String, Serializable>) result;
			}
		} else {
			logger.debug("Company  not Mandatory");
			result.put("company_id", company_id);
			result.put("status", "success");
			return (Map<String, Serializable>) result;
		}

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
				return null;
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
				return null;
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
				return null;
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

}
