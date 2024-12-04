package com.example.dians2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/issuer-details")
public class IssuerController {

    @GetMapping
    public String getIssuerDetails() {
        return "main";
    }

}

