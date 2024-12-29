package com.example.dians2.model.enumerations;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}

