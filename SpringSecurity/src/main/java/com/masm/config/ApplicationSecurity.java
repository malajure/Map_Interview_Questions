package com.masm.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// auth.inMemoryAuthentication().withUser("san").password("san").roles("ADMIN");

		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username,password,active from users where username=?")
				.authoritiesByUsernameQuery(
						"select u.username,role.name from users u join user_roles ur on u.id = ur.user_id "
								+ " join roles role on role.id = ur.role_id where u.username=?")
				.passwordEncoder(getPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * http.authorizeRequests(). antMatchers("/admin").hasAnyRole("ADMIN")
		 * .antMatchers("/user").hasAnyRole("ADMIN,USER")
		 * .antMatchers("/moderator").hasRole("MODERATOR")
		 * .antMatchers("/").permitAll().and().formLogin();
		 */
		http.authorizeRequests()
		.antMatchers("/", "/register").permitAll()
		.antMatchers("/user").hasRole("ADMIN,USER")
		.antMatchers("/moderator").hasRole("ADMIN,MODERATOR").antMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated();

		http.csrf().disable();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
