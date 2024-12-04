package com.example.dians2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EntityScan(basePackages ="com.example.dians2.model")
public class Dians2Application {

    public static void main(String[] args) {
        SpringApplication.run(Dians2Application.class, args);
    }

}
