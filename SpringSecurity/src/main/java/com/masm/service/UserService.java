package com.masm.service;

import java.io.Serializable;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masm.dao.UserDao;

@Service
public class UserService {

	@Autowired
	UserDao userDao;

	public Map<String, Serializable> createUser(@Valid Map<String, Serializable> uData) {
		return userDao.createUser(uData);

	}

	public boolean existsByUsername(String userName) {
		return userDao.existsByUsername(userName);
	}

	public boolean existsByEmail(String email) {
		return userDao.existsByEmail(email);
	}

}
