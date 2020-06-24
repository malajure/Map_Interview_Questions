package com.example.TestApi.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.example.TestApi.service.CsvFileUploadService;
import com.example.TestApi.service.UserModelService;

@Component
@Primary
public class UserModeldao {

	@Autowired
	protected JdbcTemplate jdbc;
	@Autowired
	CsvFileUploadService csvFileUploadService;

	@Value("${stakeholder.mandatory.username}")
	protected boolean usernameCheck;

	private static Log logger = LogFactory.getLog(UserModeldao.class);

	
	public int getCompanyIDByName(String companyCd) {
		try {
			String sql = "SELECT company_id FROM company_m where lower(name)=lower(\'"
					+ StringEscapeUtils.escapeSql(companyCd) + "\')";
			logger.debug("ckeckinggggggg beforee" + sql);
			return jdbc.queryForObject(sql, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}
	public List<Map<String, Object>> getAllSapData() {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		try {
			String sql = "SELECT trim(s.personnelNumber) personnelNumber,trim(s.companyCode) companyCode,trim(s.gender) gender"
					+ ",trim(s.firstName) firstName,trim(s.lastName) lastName,trim(s.dateOfJoining) dateOfJoining,"
					+ " trim(s.dateOfLeaving) dateOfLeaving,trim(s.dateOfBirth) dateOfBirth,trim(s.statusCode) statusCode"
					+ ",trim(s.locationCode) locationCode,trim(s.locationText) locationText,trim(s.reportsToEmployee) reportsToEmployee"
					+ ",trim(s.emailID) emailID,trim(s.designationDescription) designationDescription"
					+ ",trim(s.regionDescription) regionDescription,trim(s.mobileNumber) mobileNumber,trim(s.companyName) companyName,"
					+ " trim(s.departmentCode) departmentCode,trim(s.departmentName) departmentName"
					+ ",trim(s.designationCode) designationCode,trim(s.level) level,trim(s.positionID) positionID,"
					+ " trim(s.positionName) positionName,trim(s.ADID) ADID,trim(s.LHID) LHID"
					+ ",trim(s.jobFamilyGroup) jobFamilyGroup,trim(s.jobFamily) jobFamily,trim(s.jobFunction) jobFunction"
					+ " FROM sap_data s join onboard_groups og on trim(s.jobFamilyGroup)=trim(og.jobFamilyGroup)";
			return jdbc.queryForList(sql);
		} catch (EmptyResultDataAccessException e) {
			return Collections.EMPTY_LIST;
		}
	}

	public Map<String, Serializable> preprocessing(Map<String, Serializable> response) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		try {
			String sql = "truncate table user_creation_status";
			jdbc.execute(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "failed");
			result.put("responseMsg", "error in pre-processing");

		}
		return result;
	}

	public void activateuser(String user_id) {
		String sql = "update user_data set status='activated' where user_id=?";
		jdbc.update(sql, new Object[] { user_id });

	}

	public void insertToUserCreationStatus(String user_id, String status) {
		final String query = "insert into user_creation_status(user_id,status) values(?,?)";

		jdbc.update(query, new Object[] { user_id, status });
	}

	public boolean deactivateuser(String user_id) {
		if (personExists3(user_id)) {
			String sql = "delete from preregisteruser where user_id=?";
			jdbc.update(sql, new Object[] { user_id });
			sql = "update user_data set status='suspended' where user_id=?";
			jdbc.update(sql, new Object[] { user_id });
		}
		return true;
	}

	public boolean personExists3(String user_id) {
		try {
			String sql = "SELECT user_id FROM preregisteruser where user_id='" + user_id
					+ "' union select user_id from user_data where user_id='" + user_id + "'";

			Map<String, Object> userMap = jdbc.queryForMap(sql);
			if (userMap != null && !userMap.isEmpty())
				return true;
			return false;
		} catch (EmptyResultDataAccessException e) {
			return false;

		}
	}

	public String getExtraFieldDescription(String key) {
		String sql = "select ifnull(description,'null') from key_desc where `key`=?";

		try {
			return jdbc.queryForObject(sql, new Object[] { key }, String.class);
		} catch (EmptyResultDataAccessException e) {
			return "";
		}

	}

	public Map<String, Serializable> postprocessing(Map<String, Serializable> props) {
		Map<String, Serializable> response = new HashMap<String, Serializable>();
		try {
			String sql_count = "select count(distinct(user_id)) from user_creation_status";
			int exists = jdbc.queryForObject(sql_count, Integer.class);
			logger.debug("user_id's in user_creation_status table is" + exists);
			if (exists == 0) {
				logger.debug("error in postprocessing");
			} else {
				// int companyid = props.containsKey("company_id") ? (int)
				// props.get("company_id") : 0;
				response.put("company_nm", "Frontizo Business Services Private Ltd");
				List<Integer> companies = jdbc.queryForList("select company_id from company_m ", Integer.class);
				List<Map<String, Object>> suspendeddata = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> deleteddata = new ArrayList<Map<String, Object>>();
				for (Integer companyid : companies) {
					String sql = "select  pre.email,pre.user_id  from preregisteruser pre left join user_creation_status u on "
							+ "u.user_id=pre.user_id where (pre.user_id !='admin' and pre.user_id not like 'demo%' and pre.company_id="
							+ companyid + " and u.user_id is null)";
					logger.debug(sql);
					deleteddata.addAll(jdbc.queryForList(sql));
					// logger.debug(deleteddata);
					response.put("deletedpreregisterusers", (Serializable) deleteddata);
					sql = "delete pre from preregisteruser pre left join user_creation_status u on "
							+ "u.user_id=pre.user_id where (pre.user_id !='admin' and pre.user_id not like 'demo%' and pre.company_id=? "
							+ "and u.user_id is null) ";
					jdbc.update(sql, new Object[] { companyid });
					sql = "select c.email,c.user_id  from user_data c left join user_creation_status u on u.user_id=c.user_id "
							+ "where (c.user_id !='admin' and c.user_id not like 'demo%' and c.company_id=" + companyid
							+ " and c.status='activated' " + "and u.user_id is null)";
					logger.debug(sql);
					suspendeddata.addAll(jdbc.queryForList(sql));
					// logger.debug(suspendeddata);
					response.put("suspendedUsers", (Serializable) suspendeddata);
					sql = "update user_data c left join user_creation_status u on u.user_id=c.user_id "
							+ "set c.status = 'suspended' where (c.user_id !='admin' and c.user_id not like 'demo%' "
							+ "and c.company_id=? and c.status='activated' and u.user_id is null) ";
					jdbc.update(sql, new Object[] { companyid });
				}
			}
		} catch (Exception e) {
			logger.debug("exception in postprocessing");
			response.put("status", "failed");
			return response;

		}
		// logger.debug(response);
		response.put("status", "success");
		return response;

	}

	public String getBusinessUnitID(String buName) {
		try {
			String sql = "SELECT bu_id FROM business_unit_m where lower(bu_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(buName.toLowerCase()) + "\') limit 1";
			return jdbc.queryForObject(sql, String.class);
		} catch (Exception e) {
			logger.error(e);
			return "0";
		}

	}

	public Integer getBandIDByName(String bandName) {
		try {
			String sql = "SELECT band_id FROM band_m where lower(band_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(bandName.toLowerCase()) + "\')";
			logger.debug("sql is" + sql);
			return jdbc.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			logger.error("the error is : ", e);
			return 0;
		}
	}

	public Integer getLocationByName(String location) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "SELECT location_id FROM location_m where lower(loc_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(location.toLowerCase()) + "\')";
			return jdbc.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}

	public Integer getDesgnIDByName(String desgnName) {
		try {
			String sql = "SELECT desgn_id FROM designation_m where lower(desgn_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(desgnName.toLowerCase()) + "\')";
			return jdbc.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}

	}

	public Integer getFuncIDByName(String funcName) {
		if (funcName.trim().isEmpty()) {
			return 0;
		}
		try {
			String sql = "SELECT func_id FROM function_m where lower(replace(func_name,' ',''))= lower(\'"
					+ StringEscapeUtils.escapeSql(funcName.toLowerCase().replaceAll(" ", "")) + "\')";
			return jdbc.queryForObject(sql, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			insertToOnboardGroups(funcName);
			final String query = "insert into function_m(func_name) values(?)";

			final PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
					final PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, funcName);
					return ps;
				}
			};
			// The newly generated key will be saved in this object
			final KeyHolder holder = new GeneratedKeyHolder();
			jdbc.update(psc, holder);
			return holder.getKey().intValue();
		}

	}

	public void insertToOnboardGroups(String name) {
		final String query = "insert into onboard_groups(jobFamilyGroup) values(?)";

		jdbc.update(query, new Object[] { name });
	}

	public Integer getSubfuncIDByName(String subfuncName) {
		try {
			String sql = "SELECT subfunc_id FROM subfunction_m where lower(subfunc_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(subfuncName.toLowerCase()) + "\')";
			return jdbc.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}

	}

	public Map<String, Object> getCompany(Integer company_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from company_m where company_id = " + company_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;

	}

	public Integer getDeptIDByName(String deptName) {
		try {
			String sql = "SELECT dept_id FROM department_m where lower(dept_name)= lower(\'"
					+ StringEscapeUtils.escapeSql(deptName.toLowerCase()) + "\')";
			return jdbc.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}

	}

	public boolean personExists4(String email, String user_id) {
		try {
			logger.debug("email exists or not in preregisteruser");
			String sql = "SELECT count(*) FROM preregisteruser where email='" + email + "'and user_id not in ('"
					+ user_id + "')";
			logger.debug("sql=" + sql);
			int exists = jdbc.queryForObject(sql, Integer.class);
			if (exists != 0) {
				logger.debug("email already mapped to another  preregisteruser" + email);
				return true;
			} else {
				logger.debug("email exists or not in user data");
				sql = "SELECT count(*)  FROM user_data where email='" + email + "'and user_id not in ('" + user_id
						+ "')";
				logger.debug("sql=" + sql);
				exists = jdbc.queryForObject(sql, Integer.class);
				if (exists != 0) {
					logger.debug("email already mapped to another  preregisteruser" + email);
					return true;
				}
			}
			logger.debug("email not  exists in user data" + email);
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, Serializable> updateUserData2(String user_id2, Integer band_id, String grade, String bu_id,
			Integer company_id, Integer location_id, String email, String firstname, String lastname,
			String phonenumber, String gender, Integer department, Integer designation, Integer fun_id,
			Map<String, Serializable> props) {
		List<Map<String, Object>> extrasList = props.containsKey("extra")
				? (List<Map<String, Object>>) props.get("extra")
				: Collections.emptyList();

		String table_name = "";
		String extraTableName = "";
		String selectSql = "select * from user_data where user_id ='" + user_id2 + "'";
		try {
			logger.debug("in child user-data----    " + selectSql);
			jdbc.queryForMap(selectSql);
			table_name = "user_data";
			extraTableName = "user_extra";
		} catch (EmptyResultDataAccessException e) {
			selectSql = "select * from preregisteruser where user_id ='" + user_id2 + "'";
			try {
				logger.debug("in child addPreregisteredUser2----    " + selectSql);
				jdbc.queryForMap(selectSql);
				table_name = "preregisteruser";
				extraTableName = "pre_register_extra";
			} catch (EmptyResultDataAccessException e1) {
				logger.error("EmptyResultDataAccessException");
				Map<String, Serializable> creationMap = csvFileUploadService.verifyAccountV2(props, true);
				logger.debug("AAAAAAAAAAAAAAAAAAAAAAA" + creationMap);
				logger.debug("bbbbbbbbbbbbbbbbbbbbbbbbbb" + props);
				if (creationMap.containsKey("status") && creationMap.get("status").toString().equals("success")) {
					creationMap.put("flag", "created");
				}
				return creationMap;
			}
		}

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		logger.debug("user_id:" + user_id2 + " band_id:" + band_id + " grade:" + grade + " bu_id:" + bu_id
				+ "company_id:" + company_id + " location_id:" + location_id + " email:" + email + " firstname:"
				+ firstname + " lastname:" + lastname + " phone:" + phonenumber + " gender:" + gender + "department:"
				+ department + " designation:" + designation + "function" + fun_id);
		String sql = "insert ignore business_unit_m(bu_id,bu_name) values(?,?)";
		jdbc.update(sql, new Object[] { bu_id, bu_id });
		sql = "update " + table_name + " set band_id = ?,company_id = ?,location_id = ?,email = ?,firstname = ?"
				+ ",lastname = ?," + "phone = ?,gender = ?,dept_id = ?,desgn_id = ?" + " where user_id ='" + user_id2
				+ "'";

		String extraSelect = "update " + extraTableName + " set value = ? where user_id ='" + user_id2
				+ "' and `key` =?";
		try {
			int check = jdbc.update(sql, new Object[] { band_id, company_id, location_id, email.toLowerCase(),
					firstname, lastname, phonenumber, gender, department, designation });
			logger.debug("check value is" + check);
			if (check == 1) {
				map.put("status", "success");
				map.put("user_id", user_id2);
			}
			logger.debug(extrasList);
			for (Map<String, Object> map2 : extrasList) {
				int checkExtra = 0;
				if (map2.get("value") != null && !"".equalsIgnoreCase((String) map2.get("value"))) {
					logger.debug("value is" + map2.get("value"));
					checkExtra = jdbc.update(extraSelect,
							new Object[] { map2.get("value"), map2.get("key").toString() });
					logger.debug("check extra value is" + checkExtra);
				}
				if (checkExtra == 1) {
					map.put("status", "success");
				}
			}
			logger.debug("select query" + jdbc.queryForMap(selectSql));
			map.put("flag", "updated");// updated user

		} catch (Exception e) {
			logger.error("Exception --", e);
			map.put("status", "failed");
			map.put("responseMsg", "Error in pre-registering User");
		}

		return map;
	}

	public long getMoodleId(String user_id) {
		String sql = "select moodleid from user_data where user_id=?";
		logger.debug("getMoodleId sql: " + sql);
		List<Map<String, Object>> resultset = jdbc.queryForList(sql, new Object[] { user_id });
		if (resultset == null || resultset.isEmpty())
			return 0;
		Map<String, Object> map = resultset.get(0);
		logger.debug("map:" + map);
		return (Long) (map.containsKey("moodleid") ? map.get("moodleid") : 0);
	}

	public Map<String, Object> getRegisterAccount2(String email) {

		try {
			String sql = "select firstname firstName,email verified from preregisterUser " + "where email =?";
			logger.debug("verification of email exists or not" + sql);
			return jdbc.queryForMap(sql, new Object[] { email });
		} catch (Exception e) {
			try {
				String sql1 = "select firstname firstName,email verified from user_data " + "where email =?";
				logger.debug("verification of email exists or not" + sql1);
				return jdbc.queryForMap(sql1, new Object[] { email });
			} catch (Exception exception) {
				return Collections.EMPTY_MAP;
			}
		}
	}

	public Map<String, Object> getDepartment(Integer department) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from department_m where dept_id = " + department;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getDesignation(Integer designation) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from designation_m where desgn_id = " + designation;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getDeptDesgnMap(Integer department, Integer designation) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "SELECT map_id FROM dept_desgn_mapping where dept_id = " + department + " and desgn_id = "
					+ designation;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getFunction(Integer fun_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from function_m where func_id = " + fun_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getSubfunction(Integer sub_fun_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from subfunction_m where subfunc_id = " + sub_fun_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getfuncSubfuncMap(Integer fun_id, Integer sub_fun_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "SELECT map_id FROM func_subfunc_mapping where func_id = " + fun_id + " and subfunc_id = "
					+ sub_fun_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getBand(Integer band_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from band_m where band_id = " + band_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getBu(String bu_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from business_unit_m where bu_id = '" + bu_id + "'";
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Object> getLocation(Integer location_id) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "select * from location_m where location_id = " + location_id;
			Map<String, Object> actionStatus = jdbc.queryForMap(sql);
			result.put("status", "success");
		} catch (Exception e) {
			result.put("status", "Failed");
		}
		return result;
	}

	public Map<String, Serializable> addPreregisteredUser2(String user_id2, int band_id, String grade, String bu_id,
			int company_id, int location_id, String email, String firstname, String lastname, String phonenumber,
			String gender, int department, int designation, int fun_id, int sub_fun_id, String activationtoken,
			String verified) {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		logger.debug("user_id:" + user_id2 + " band_id:" + band_id + " grade:" + grade + " bu_id:" + bu_id
				+ "company_id:" + company_id + " location_id:" + location_id + " email:" + email + " firstname:"
				+ firstname + " lastname:" + lastname + " phone:" + phonenumber + " gender:" + gender + "department:"
				+ department + " designation:" + designation + "function" + fun_id + "sub_function" + sub_fun_id
				+ " activationToken:" + activationtoken);
		String sql = "insert ignore business_unit_m(bu_id,bu_name) values(?,?)";
		jdbc.update(sql, new Object[] { bu_id, bu_id });
		if (usernameCheck) {
			sql = "insert into preregisterUser"
					+ "(user_id,band_id,grade,bu_id,company_id,location_id,email,firstname,lastname,"
					+ "phone,gender,dept_id,desgn_id,func_id,subfunc_id,activationtoken,verified) "
					+ "values(?,if(?=0,default(band_id),?),?,?,?,if(?=0,default(location_id),?),"
					+ "?,?,?,?,?,if(?=0,default(dept_id),?),if(?=0,default(desgn_id),?),"
					+ "if(?=0,default(func_id),?),if(?=0,default(subfunc_id),?),?,?)";
			try {
				int check = jdbc.update(sql,
						new Object[] { user_id2, band_id, band_id, grade, bu_id, company_id, location_id, location_id,
								email.toLowerCase(), firstname, lastname, phonenumber, gender, department, department,
								designation, designation, fun_id, fun_id, sub_fun_id, sub_fun_id, activationtoken,
								verified });
				if (check == 1) {
					map.put("status", "success");
					map.put("user_id", user_id2);
				}

			} catch (Exception e) {
				logger.error("Exception in ", e);
				map.put("status", "failed");
				map.put("responseMsg", "Error in pre-registering User");
			}

		} else {
			sql = "insert into preregisterUser"
					+ "(band_id,grade,bu_id,company_id,location_id,email,firstname,lastname,"
					+ "phone,gender,dept_id,desgn_id,func_id,subfunc_id,activationtoken,verified) "
					+ "values(if(?=0,default(band_id),?),?,?,?,if(?=0,default(location_id),?),"
					+ "?,?,?,?,?,if(?=0,default(dept_id),?),if(?=0,default(desgn_id),?),"
					+ "if(?=0,default(func_id),?),if(?=0,default(subfunc_id),?),?,?)";

			logger.debug("query:" + sql);
			try {
				int check = jdbc.update(sql,
						new Object[] { band_id, band_id, grade, bu_id, company_id, location_id, location_id,
								email.toLowerCase(), firstname, lastname, phonenumber, gender, department, department,
								designation, designation, fun_id, fun_id, sub_fun_id, sub_fun_id, activationtoken,
								verified });
				if (check == 1) {
					String sql1 = "SELECT id from table_user_id order by id desc limit 1";
					String user_id = jdbc.queryForObject(sql1, String.class);
					map.put("status", "success");
					map.put("user_id", "user" + user_id);
				}

			} catch (Exception e) {
				logger.error("Exception in ", e);
				map.put("status", "failed");
				map.put("responseMsg", "Error in pre-registering User");
			}
		}
		return map;
	}

	public void insertPreRegisterExtra(String user_id, String key, String value) {
		String sql = "insert ignore into pre_register_extra(`user_id`, `key`, `value`) values(?,?,?) ON DUPLICATE KEY UPDATE `value` = ?";
		jdbc.update(sql, new Object[] { user_id, key.trim(), value, value });
	}

	public String getEmailByUserName(String user_id) {
		String email = "";
		try {
			String sql = "select email from user_data where user_id=?";
			email = jdbc.queryForObject(sql, new Object[] { user_id }, String.class);
		} catch (Exception e) {
			logger.error("the error is : ", e);
			email = "";
		}
		return email;
	}

	public Map<String, Object> checkPreregisterData(String userID) {
		// TODO Auto-generated method stub
		try {
			String sql = "select * from preregisteruser where user_id='" + userID + "'";
			return jdbc.queryForMap(sql);
		} catch (EmptyResultDataAccessException e) {
			return Collections.EMPTY_MAP;
		}

	}
	
	public void updateMoodleUser(String user_id, int id) {
		String sql = "update user_data set moodleid=? where user_id=?";
		jdbc.update(sql, new Object[] { id, user_id });

	}
}
