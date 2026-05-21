package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Size(max = 100)
    @Schema(description = "Email do usuário", example = "admin@example.com")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 100)
    @Schema(description = "Nome de usuário", example = "admin")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 255)
    @Schema(description = "Senha (mínimo 6 caracteres)", example = "123456")
    private String password;

    @NotNull(message = "Role is mandatory")
    @Schema(description = "Função no sistema", example = "ADMIN", allowableValues = {"ADMIN", "TEACHER", "STUDENT"})
    private Role role;
}
