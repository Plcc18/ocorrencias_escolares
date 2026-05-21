package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.repository.UserRepository;
import com.example.ocorrencias_escolares_api.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username já em uso: " + request.getUsername());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }
}
