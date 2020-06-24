package com.masm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masm.Dto.UserRole;
import com.masm.dao.RoleDao;

@Service
public class RoleService {

	@Autowired
	RoleDao roleDao;

	public String getRoleByUserName(UserRole role) {
		return roleDao.getRoleByUserName(role);
	}

}
