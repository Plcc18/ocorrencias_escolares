package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.AuthResponse;
import com.example.ocorrencias_escolares_api.dto.LoginRequest;
import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.dto.UserResponseDTO;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.security.JwtTokenProvider;
import com.example.ocorrencias_escolares_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints de login, registro e perfil")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getUsername(), user.getRole()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(user, UserResponseDTO.class));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Retorna os dados do usuário autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDTO.class));
    }
}