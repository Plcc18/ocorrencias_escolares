package com.example.ocorrencias_escolares_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Schema(description = "Nome de usuário para cadastro", example = "pedrolucas")
    private String username;

    @NotBlank
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;

    @NotBlank
    @Schema(description = "Função do usuário no sistema (ex: ADMIN, TEACHER, STUDENT)", example = "ADMIN")
    private String role;
}
