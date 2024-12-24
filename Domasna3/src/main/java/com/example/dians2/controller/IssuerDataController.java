package com.example.dians2.controller;

import com.example.dians2.model.IssuerData;
import com.example.dians2.service.impl.CodeServiceImpl;
import com.example.dians2.service.impl.IssuerDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/issuers")
public class IssuerDataController {

    private final IssuerDataServiceImpl issuerService;
    private final CodeServiceImpl codeService;

    @Autowired
    public IssuerDataController(IssuerDataServiceImpl issuerService, CodeServiceImpl codeService) {
        this.issuerService = issuerService;
        this.codeService = codeService;
    }

    @GetMapping()
    public String getCodes(Model model) {
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        return "main";
    }

    @PostMapping("/details")
    public String handleFormSubmit(@RequestParam String code, @RequestParam String from, @RequestParam String to, Model model) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<IssuerData> issuers = issuerService.getIssuersByCodeAndTimeRange(code, fromDate, toDate);

        System.out.println(issuers);
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        model.addAttribute("issuers", issuers);
        model.addAttribute("selectedCode", code);
        model.addAttribute("selectedFrom", from);
        model.addAttribute("selectedTo", to);
        return "main";
    }

    @PostMapping("/details/graph")
    public String viewGraph(@RequestParam String code, @RequestParam String from, @RequestParam String to, Model model) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<IssuerData> issuers = issuerService.getIssuersByCodeAndTimeRange(code, fromDate, toDate);

        List<Date> dates = issuers.stream().map(IssuerData::getDate).collect(Collectors.toList());

        // Parse the last trade prices because they are saved as strings
        NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
        List<Double> lastTradePrices = issuers.stream()
                .map(issuer -> {
                    try {
                        return format.parse(issuer.getLastTradePrice()).doubleValue();
                    } catch (ParseException e) {
                        throw new RuntimeException("Failed to parse last trade price: " + issuer.getLastTradePrice(), e);
                    }
                })
                .collect(Collectors.toList());

        model.addAttribute("dates", dates);
        model.addAttribute("lastTradePrices", lastTradePrices);
        model.addAttribute("selectedCode", code);
        model.addAttribute("selectedFrom", from);
        model.addAttribute("selectedTo", to);

        return "graph-page";
    }

}



