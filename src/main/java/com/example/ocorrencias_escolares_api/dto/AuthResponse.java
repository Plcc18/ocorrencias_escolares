package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthResponse {

    @Schema(description = "JWT token de acesso")
    private String accessToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    private final String tokenType = "Bearer";

    @Schema(description = "ID do usuário autenticado")
    private Long userId;

    @Schema(description = "Email do usuário autenticado")
    private String email;

    @Schema(description = "Função do usuário")
    private Role role;

    public AuthResponse(String accessToken, Long userId, String email, Role role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}
