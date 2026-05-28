package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.LoginRequest;
import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.enums.Role;
import com.example.ocorrencias_escolares_api.config.LoginRateLimitFilter;
import com.example.ocorrencias_escolares_api.config.SecurityConfig;
import com.example.ocorrencias_escolares_api.security.CustomUserDetailsService;
import com.example.ocorrencias_escolares_api.security.JwtTokenProvider;
import com.example.ocorrencias_escolares_api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, LoginRateLimitFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private org.modelmapper.ModelMapper modelMapper;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setUsername("admin");
        adminUser.setPassword("hashed");
        adminUser.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("POST /api/auth/login - sucesso retorna token e dados do usuário")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("123456");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(adminUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtTokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login - credenciais inválidas retorna 401")
    void login_badCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("errada");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - email inválido retorna 400")
    void login_invalidEmail_returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("nao-e-email");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login - campos obrigatórios ausentes retorna 400")
    void login_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - sucesso retorna 201")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("novo@example.com");
        request.setUsername("novousuario");
        request.setPassword("123456");
        request.setRole(Role.TEACHER);

        when(userService.register(any(RegisterRequest.class))).thenReturn(adminUser);
        when(modelMapper.map(any(), any())).thenReturn(new com.example.ocorrencias_escolares_api.dto.UserResponseDTO());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/auth/register - senha curta retorna 400")
    void register_shortPassword_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("novo@example.com");
        request.setUsername("novousuario");
        request.setPassword("123"); // menor que 6
        request.setRole(Role.TEACHER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
