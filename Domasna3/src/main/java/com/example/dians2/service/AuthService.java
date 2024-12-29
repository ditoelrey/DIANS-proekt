package com.example.dians2.service;

import com.example.dians2.model.User;

public interface AuthService {

    User login(String username, String password);

}