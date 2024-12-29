package com.example.dians2.service;

import com.example.dians2.model.User;

import java.util.Optional;

import com.example.dians2.model.enumerations.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String name, String surname);
}


