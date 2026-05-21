package com.example.ocorrencias_escolares_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Schema(description = "Email cadastrado no sistema", example = "admin@example.com")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
