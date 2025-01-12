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
    private static ComparisonController instance;

    public ComparisonController(CodeServiceImpl codeService) {
        this.codeService = codeService;
    }
    public static synchronized ComparisonController getInstance(CodeServiceImpl codeService) {
        if (instance == null) {
            instance = new ComparisonController(codeService);
        }
        return instance;
    }
    @GetMapping()
    public String showComparisonPage(Model model) {
        model.addAttribute("codes", codeService.getAllCodes());
        return "comparison";
    }
}
