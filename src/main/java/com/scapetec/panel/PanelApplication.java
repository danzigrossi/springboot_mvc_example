package com.scapetec.panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = {"com.scapetec"})
@EnableJpaRepositories(basePackages = {"com.scapetec"})
@ComponentScan(basePackages = {"com.scapetec"})
public class PanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(PanelApplication.class, args);
	}
}
