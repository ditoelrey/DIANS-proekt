package com.example.dians2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackages ="com.example.dians2.model")
public class Dians2Application {

    public static void main(String[] args) {
        SpringApplication.run(Dians2Application.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder(10);
    }

}
