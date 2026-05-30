package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthResponse {
    @Schema(description = "JWT token de acesso")
    private String accessToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "ID do usuário autenticado")
    private Long userId;

    @Schema(description = "Email do usuário autenticado")
    private String email;

    @Schema(description = "Nome de usuário")
    private String username;

    @Schema(description = "Função do usuário")
    private Role role;

    @Schema(description = "ID do professor (apenas para role TEACHER)")
    private Long teacherId;

    public AuthResponse(String accessToken, Long userId, String email,
                        String username, Role role, Long teacherId) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.role = role;
        this.teacherId = teacherId;
    }
}