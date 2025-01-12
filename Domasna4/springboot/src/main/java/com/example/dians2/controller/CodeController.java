package com.example.dians2.controller;

import com.example.dians2.repository.CodeRepository;
import com.example.dians2.repository.IssuerDataRepository;
import com.example.dians2.service.CodeService;
import com.example.dians2.service.impl.CodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CodeController{

    private final CodeServiceImpl codeService;

    @Autowired
    public CodeController(CodeRepository codeRepository, IssuerDataRepository issuerDataRepository) {
        this.codeService = CodeServiceImpl.getInstance(codeRepository, issuerDataRepository);
    }
    @GetMapping("/technical-analysis")
    public String getCodes(Model model) {
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        return "technical-analysis";
    }
}
