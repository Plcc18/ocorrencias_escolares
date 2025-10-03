package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.repository.UserRepository;
import com.example.ocorrencias_escolares_api.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exist");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword((passwordEncoder.encode(request.getPassword())));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }


}
