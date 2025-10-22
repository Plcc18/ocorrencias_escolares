package com.example.ocorrencias_escolares_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is mandatory")
    @Schema(description = "Email cadastrado no sistema", example = "pedrolucas@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Nome de usuário cadastrado no sistema", example = "pedrolucas")
    private String username;

    @NotBlank
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
