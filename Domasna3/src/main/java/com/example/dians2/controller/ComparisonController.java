package com.example.dians2.controller;

import com.example.dians2.service.impl.CodeServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comparison")
public class ComparisonController {

    private final CodeServiceImpl codeService;

    public ComparisonController(CodeServiceImpl codeService) {
        this.codeService = codeService;
    }
    @GetMapping()
    public String showComparisonPage(Model model) {
        model.addAttribute("codes", codeService.getAllCodes());
        return "comparison";
    }
}
