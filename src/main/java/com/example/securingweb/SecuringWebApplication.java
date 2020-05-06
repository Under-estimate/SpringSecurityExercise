package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecuringWebApplication {

	public static void main(String[] args){
		Utils.loadUsers();
		SpringApplication.run(SecuringWebApplication.class, args);
	}

}
