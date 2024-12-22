package com.example.dians2.service.impl;

import com.example.dians2.model.User;
import com.example.dians2.model.enumerations.Role;
import com.example.dians2.model.exceptions.InvalidArgumentException;
import com.example.dians2.model.exceptions.PasswordsDoNotMatchException;
import com.example.dians2.model.exceptions.UsernameAlreadyExistsException;
import com.example.dians2.repository.UserRepository;
import com.example.dians2.repository.UserRepository;
import com.example.dians2.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentException();
        }
        return userRepository.findByUsernameAndPassword(username,
                password).orElseThrow();
    }




}
