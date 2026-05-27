package com.example.ocorrencias_escolares_api.config;

import com.example.ocorrencias_escolares_api.entity.User;
import com.example.ocorrencias_escolares_api.enums.Role;
import com.example.ocorrencias_escolares_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminProperties adminProperties;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminProperties.getEmail()).isEmpty()) {

            User admin = new User();
            admin.setUsername(adminProperties.getName());
            admin.setEmail(adminProperties.getEmail());
            admin.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println("✅ Admin criado: " + adminProperties.getEmail());
        }
    }
}
