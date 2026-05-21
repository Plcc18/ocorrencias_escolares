package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.enums.Role;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.repository.UserRepository;
import com.example.ocorrencias_escolares_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setEmail("admin@example.com");
        request.setUsername("admin");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("register - sucesso quando email e username são únicos")
    void register_success() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed");

        User saved = new User();
        saved.setId(1L);
        saved.setEmail(request.getEmail());
        saved.setUsername(request.getUsername());
        saved.setPassword("hashed");
        saved.setRole(Role.ADMIN);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = service.register(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("register - lança BusinessException quando email já existe")
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - lança BusinessException quando username já existe")
    void register_duplicateUsername_throwsException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Username já em uso");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - senha é armazenada como hash, nunca em texto plano")
    void register_passwordIsHashed() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$hashed");

        User saved = new User();
        saved.setPassword("$2a$hashed");
        saved.setRole(Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = service.register(request);

        assertThat(result.getPassword()).isNotEqualTo("123456");
        assertThat(result.getPassword()).isEqualTo("$2a$hashed");
    }
}
