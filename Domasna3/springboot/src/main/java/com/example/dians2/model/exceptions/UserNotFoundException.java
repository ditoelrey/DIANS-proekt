package com.example.dians2.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username){
        super(String.format("User with username: %s was not found", username));
    }
    public UserNotFoundException(Long id){
        super(String.format("User with id: %d was not found", id));
    }
}