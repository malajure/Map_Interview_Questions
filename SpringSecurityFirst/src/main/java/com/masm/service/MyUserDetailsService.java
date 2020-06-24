package com.masm.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.masm.model.MyUserDetails;
import com.masm.model.User;
import com.masm.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

//get JPA Repo
	@Autowired
	UserRepository userRepository;

	protected static Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		// return new MyUserDetails(userName);// username passed from UI

		logger.info("before db " + userName);

		Optional<User> user = userRepository.findByUserName(userName);
		logger.info("after db" + user.get().getUserName());

		user.orElseThrow(() -> new UsernameNotFoundException("user name not found" + userName));

		return user.map(MyUserDetails::new).get();

	}

}
