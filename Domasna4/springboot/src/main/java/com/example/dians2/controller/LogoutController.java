package com.example.dians2.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogoutController {

    private static LogoutController instance;
    private LogoutController() {}
    public static synchronized LogoutController getInstance() {
        if (instance == null) {
            instance = new LogoutController();
        }
        return instance;
    }

    @GetMapping
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
