package com.example.dians2.controller;

import com.example.dians2.service.impl.CodeServiceImpl;
import com.example.dians2.service.impl.SentimentAnalysisServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sentiment-analysis")
public class SentimentAnalysisController {

    private final SentimentAnalysisServiceImpl sentimentAnalysisService;
    private final CodeServiceImpl codeService;
    private static SentimentAnalysisController instance;

    public SentimentAnalysisController(SentimentAnalysisServiceImpl sentimentAnalysisService, CodeServiceImpl codeService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
        this.codeService = codeService;
    }
    public static synchronized SentimentAnalysisController getInstance(SentimentAnalysisServiceImpl sentimentAnalysisService, CodeServiceImpl codeService) {
        if (instance == null) {
            instance = new SentimentAnalysisController(sentimentAnalysisService, codeService);
        }
        return instance;
    }

    @GetMapping
    public String getSentimentAnalysisForm(Model model) {
        model.addAttribute("codes", codeService.getAllCodes());
        return "sentiment-analysis";
    }

    @GetMapping("/results")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSentimentAnalysis(@RequestParam String issuer) {
        try {

            List<SentimentAnalysisServiceImpl.SentimentResult> sentimentResults = sentimentAnalysisService.analyseSentiment(issuer);

            return ResponseEntity.ok(Map.of("results", sentimentResults));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error fetching sentiment analysis"));
        }
    }
}