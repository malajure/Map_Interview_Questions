package com.masm.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.masm.Dto.UserRole;
import com.masm.service.RoleService;

@Repository
public class UserDao {

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	RoleService roleService;

	public Map<String, Serializable> createUser(@Valid Map<String, Serializable> uData) {

		Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
		KeyHolder keyHolder =  new GeneratedKeyHolder();

		int key = jdbcTemplate.update("insert into users(email,password,username,active) values(?,?,?,?)",
				uData.get("email"), uData.get("password"), uData.get("username"), uData.get("active"),keyHolder);

		if (key != 0) {
			
			//roleService.getRoleByUserName(uData.get("role"))
			
			Set<String> roleSet = (Set) uData.get("role");
			
			for(String role:roleSet) {
				if(role.equalsIgnoreCase(UserRole.ROLE_USER.toString())) {
					
				}
			}
			String sql ="insert into user_roles values(?,?)";
			
			jdbcTemplate.update(sql,key);
			resultMap.put("success", "success");
		} else {
			resultMap.put("fail", "fail");
		}
		return resultMap;
	}

	public boolean existsByUsername(String userName) {

		String sql = "select count(*) from users where username='" + userName + "'";
		int role = jdbcTemplate.queryForObject(sql, Integer.class);
		if (role > 0) {
			return true;
		}
		return false;

	}

	public boolean existsByEmail(String email) {
		String sql = "select count(*) from users where email='" + email + "'";
		int exists = jdbcTemplate.queryForObject(sql, Integer.class);

		if (exists > 0) {
			return true;
		}
		return false;
	}

}
