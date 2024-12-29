package com.example.dians2.controller;

import com.example.dians2.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CodeController{

    @Autowired
    private CodeService codeService;

    @GetMapping("/technical-analysis")
    public String getCodes(Model model) {
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        return "technical-analysis";
    }
}
