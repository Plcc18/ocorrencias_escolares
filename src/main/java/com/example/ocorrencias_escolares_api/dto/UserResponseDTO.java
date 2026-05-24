package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "admin@example.com")
    private String email;

    @Schema(description = "Nome de usuário", example = "admin")
    private String username;

    @Schema(description = "Função no sistema", example = "ADMIN")
    private Role role;

    @Schema(description = "Data e hora de criação da conta")
    private LocalDateTime createdAt;
}