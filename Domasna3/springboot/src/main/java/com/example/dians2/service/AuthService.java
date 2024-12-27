package com.example.dians2.service;

import com.example.dians2.model.User;

public interface AuthService {

    User login(String username, String password);

    User register(String username, String password, String repeatPassword, String name, String surname);
}