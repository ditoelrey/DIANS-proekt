package com.example.dians2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PredictedPricesController {

    @GetMapping("/predicted-prices")
    public String getPredictedPrices(Model model) {
        List<String[]> predictedPricesData = new ArrayList<>();
        try {
            Path filePath = Path.of("src/main/resources/predicted_average_prices.csv");

            // Check if the file exists before proceeding
            if (!Files.exists(filePath)) {
                System.err.println("File not found: " + filePath);
                return "error"; // Return an error page if the file doesn't exist
            }

            try (BufferedReader br = Files.newBufferedReader(filePath)) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length < 2) continue;

                    String issuerCode = parts[0].trim();
                    String predictedPriceStr = parts[1].split(" ")[0].replaceAll("[^\\d.]", "").trim(); // Extract numeric part

                    if (!predictedPriceStr.isEmpty()) {
                        try {
                            double predictedPrice = Double.parseDouble(predictedPriceStr);

                            DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                            decimalFormat.setDecimalSeparatorAlwaysShown(true);
                            String formattedPrice = decimalFormat.format(predictedPrice);

                            formattedPrice = formattedPrice.replace(',', ' ').replace('.', ',').replace(' ', '.');

                            predictedPricesData.add(new String[]{issuerCode, formattedPrice});
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing predicted price for " + parts[0] + ": " + predictedPriceStr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        model.addAttribute("tableData", predictedPricesData);
        return "predicted-prices";
    }
}