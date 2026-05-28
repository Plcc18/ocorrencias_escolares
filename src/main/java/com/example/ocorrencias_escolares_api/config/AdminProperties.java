package com.example.ocorrencias_escolares_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.admin")
@Component
public class AdminProperties {

    // Getters e Setters
    private String name;
    private String email;
    private String password;

}
