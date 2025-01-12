package com.example.dians2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class SentimentAnalysisServiceImpl {
    private static SentimentAnalysisServiceImpl instance;

    private SentimentAnalysisServiceImpl() {}
    public static synchronized SentimentAnalysisServiceImpl getInstance() {
        if (instance == null) {
            instance = new SentimentAnalysisServiceImpl();
        }
        return instance;
    }

    public List<SentimentResult> analyseSentiment(String issuer) {
        List<SentimentResult> results = new ArrayList<>();

        try {

            String pythonScriptPath = "python";
            String scriptPath = "sentiment_analysis.py";


            ProcessBuilder processBuilder = new ProcessBuilder(pythonScriptPath, scriptPath, issuer);
            processBuilder.directory(new File("src/main/resources"));
            processBuilder.redirectErrorStream(true);


            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }


            String jsonOutput = output.toString();
            System.out.println("Python output: " + jsonOutput);


            if (jsonOutput.startsWith("{") || jsonOutput.startsWith("[")) {
                results = parseSentimentResult(jsonOutput);
            } else {
                System.err.println("Invalid JSON output: " + jsonOutput);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python script execution failed! Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }

    private List<SentimentResult> parseSentimentResult(String jsonOutput) {
        List<SentimentResult> results = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SentimentResult[] resultArray = objectMapper.readValue(jsonOutput, SentimentResult[].class);
            for (SentimentResult result : resultArray) {
                results.add(result);
            }
        } catch (IOException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    public static class SentimentResult {
        public String date;
        public String sentiment;
        public String recommendation;
        public double positive_score;
        public double negative_score;
        public double neutral_score;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }

        public double getPositive_score() {
            return positive_score;
        }

        public void setPositive_score(double positive_score) {
            this.positive_score = positive_score;
        }

        public double getNegative_score() {
            return negative_score;
        }

        public void setNegative_score(double negative_score) {
            this.negative_score = negative_score;
        }

        public double getNeutral_score() {
            return neutral_score;
        }

        public void setNeutral_score(double neutral_score) {
            this.neutral_score = neutral_score;
        }
    }
}
