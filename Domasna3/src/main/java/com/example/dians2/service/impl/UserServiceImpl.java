package com.example.dians2.service.impl;

import com.example.dians2.model.User;
import com.example.dians2.repository.UserRepository;
import com.example.dians2.service.UserService;
import org.springframework.stereotype.Service;
import com.example.dians2.model.exceptions.UserNotFoundException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

}