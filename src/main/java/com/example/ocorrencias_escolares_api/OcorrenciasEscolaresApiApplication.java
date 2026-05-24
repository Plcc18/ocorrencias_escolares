package com.example.ocorrencias_escolares_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OcorrenciasEscolaresApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcorrenciasEscolaresApiApplication.class, args);
	}

}
