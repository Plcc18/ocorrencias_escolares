package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.RegisterRequest;
import com.example.ocorrencias_escolares_api.entity.User;

public interface UserService {
    User register(RegisterRequest request);
}
