package com.masm.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.masm.Dto.UserRole;

@Repository
public class RoleDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String getRoleByUserName(UserRole role) {
		String sql = "select name from roles where name='"+role+"'";
		try {
			String roleType = jdbcTemplate.queryForObject(sql,String.class);

			return roleType;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
