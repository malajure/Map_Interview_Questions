package com.masm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.masm.repository.UserRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class SpringSecurityFirstApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityFirstApplication.class, args);
	}

}
