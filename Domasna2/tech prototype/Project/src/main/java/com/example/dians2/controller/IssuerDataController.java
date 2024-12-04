package com.example.dians2.controller;

import com.example.dians2.model.IssuerData;
import com.example.dians2.service.CodeService;
import com.example.dians2.service.IssuerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class IssuerDataController {

    private final IssuerDataService issuerService;
    private final CodeService codeService;

    @Autowired
    public IssuerDataController(IssuerDataService issuerService, CodeService codeService) {
        this.issuerService = issuerService;
        this.codeService = codeService;
    }

    @PostMapping("/submit")
    public String handleFormSubmit(@RequestParam String code, @RequestParam String from, @RequestParam String to, Model model) {
        // Convert from and to time to Date or LocalDateTime
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        // Call service to retrieve data
        List<IssuerData> issuers = issuerService.getIssuersByCodeAndTimeRange(code, fromDate, toDate);

        System.out.println(issuers);
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        // Add the results to the model to display
        model.addAttribute("issuers", issuers);
        return "index";  // Return a view name to display results
    }
}

