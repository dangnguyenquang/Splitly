package com.example.splitly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
// @EnableJpaRepositories("com.example.splitly.domain.repository")
public class SplitlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitlyApplication.class, args);
	}

}
