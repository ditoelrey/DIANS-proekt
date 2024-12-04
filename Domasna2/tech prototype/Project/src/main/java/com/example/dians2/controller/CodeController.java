package com.example.dians2.controller;

import com.example.dians2.service.CodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class CodeController {

    private final CodeService codeService;

    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @GetMapping("/")
    public String getCodes(Model model) {
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        return "index"; // The name of your HTML file without the extension
    }
}

