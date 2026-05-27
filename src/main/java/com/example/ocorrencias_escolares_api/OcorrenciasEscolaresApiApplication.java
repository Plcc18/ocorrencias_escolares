package com.example.ocorrencias_escolares_api;

import com.example.ocorrencias_escolares_api.config.AdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
@EnableJpaAuditing
public class OcorrenciasEscolaresApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcorrenciasEscolaresApiApplication.class, args);
	}

}
