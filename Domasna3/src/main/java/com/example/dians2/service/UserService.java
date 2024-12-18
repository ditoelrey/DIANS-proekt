package com.example.dians2.service;

import com.example.dians2.model.User;

import java.util.Optional;

public interface UserService {

    public Optional<User> findByUsername(String username);

    public void saveUser(User user);

    User findById(Long id);

}
