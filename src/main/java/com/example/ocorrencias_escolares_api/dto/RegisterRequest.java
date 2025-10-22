package com.example.ocorrencias_escolares_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is mandatory")
    @Size(max = 100)
    @Schema(description = "Email do usuário para cadastro", example = "admin@example.com")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Size(max = 100)
    @Schema(description = "Nome de usuário para cadastro", example = "admin")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 255)
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;

    @NotBlank(message = "Role is mandatory")
    @Size(max = 20)
    @Schema(description = "Função do usuário no sistema (ex: ADMIN, USER)", example = "ADMIN")
    private String role;
}
