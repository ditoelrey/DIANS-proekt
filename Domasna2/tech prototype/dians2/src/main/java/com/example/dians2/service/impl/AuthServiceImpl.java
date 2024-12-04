package com.example.dians2.service.impl;

import com.example.dians2.model.User;
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


    @Override
    public User register(String username, String password, String repeatPassword, String name, String surname) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty())
            throw new InvalidArgumentException();
        if (!password.equals(repeatPassword))
            throw new PasswordsDoNotMatchException();
        if (this.userRepository.findByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException(username);
        User user = new User(username, password, name, surname);
        return userRepository.save(user);
    }

}
