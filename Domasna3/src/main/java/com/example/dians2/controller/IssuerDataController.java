package com.example.dians2.controller;

import com.example.dians2.model.IssuerData;
import com.example.dians2.service.impl.CodeServiceImpl;
import com.example.dians2.service.impl.IssuerDataServiceImpl;
import com.example.dians2.service.impl.TechnicalAnalysisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/issuers")
public class IssuerDataController {
    private List<Double> prices;
    private List<String> dates;
    private List<Double> buy;
    private List<Double> sell;
    private String presentIssuer;

    private final IssuerDataServiceImpl issuerService;
    private final CodeServiceImpl codeService;
    private final TechnicalAnalysisServiceImpl technicalAnalysisService;

    @Autowired
    public IssuerDataController(IssuerDataServiceImpl issuerService, CodeServiceImpl codeService, TechnicalAnalysisServiceImpl technicalAnalysisService) {
        this.issuerService = issuerService;
        this.codeService = codeService;
        this.technicalAnalysisService = technicalAnalysisService;
    }

    @GetMapping()
    public String getCodes(Model model) {
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        return "main";
    }

    @PostMapping()
    public String handleFormSubmit(@RequestParam String code, @RequestParam String from, @RequestParam String to, Model model) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<IssuerData> issuers = issuerService.getIssuersByCodeAndTimeRange(code, fromDate, toDate);
        List<String> codes = codeService.getAllCodes();
        model.addAttribute("codes", codes);
        model.addAttribute("issuers", issuers);
        model.addAttribute("selectedCode", code);
        model.addAttribute("selectedFrom", from);
        model.addAttribute("selectedTo", to);
        return "main";
    }

    @GetMapping("/stock-chart-total-data")
    @ResponseBody
    public Map<String, Object> getStockTotalData(
            @RequestParam String issuer,
            @RequestParam String timePeriod) {
        List<IssuerData>issuerData=issuerService.sortIssuersByDate(issuer);
        List<String> periodDates = new ArrayList<>();
        List<Double> periodAvgPrices = new ArrayList<>();

        int differenceCoefficient = calculateDifferenceCoefficient(timePeriod);

        int coefficient = getDateCoefficient(issuerData.get(0).getDate().toString());
        for(IssuerData row: issuerData){
            int rowCoefficient = getDateCoefficient(row.getDate().toString());
            if(rowCoefficient < coefficient - differenceCoefficient){
                break;
            }

            periodDates.add(row.getDate().toString());
            periodAvgPrices.add(parseNumber(row.getAvgPrice()));
        }
        Collections.reverse(periodDates);
        Collections.reverse(periodAvgPrices);

        return Map.of(
                "periodDates", periodDates,
                "periodAvgPrices", periodAvgPrices
        );
    }

    private int calculateDifferenceCoefficient(String timePeriod) {
        if(timePeriod.equals("1_day"))
            return 1;
        if(timePeriod.equals("1_week"))
            return 7;
        if(timePeriod.equals("1_month"))
            return 30;
        if(timePeriod.equals("6_months"))
            return 30 * 6;
        if(timePeriod.equals("1_year"))
            return 365;
        if(timePeriod.equals("5_years"))
            return 365 * 5;
        return 3650;
    }
    private int getDateCoefficient(String date){
        String parts[] = date.split("-");
        return Integer.parseInt(parts[1]) * 30 + Integer.parseInt(parts[2]) + Integer.parseInt(parts[0]) * 365;
    }
    private int getDateCoefficientTemp(String date){

        String parts[] = date.split("/");
        return Integer.parseInt(parts[0]) * 30 + Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]) * 365;
    }

    public double parseNumber(String numberStr) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
            Number number = format.parse(numberStr);
            return number.doubleValue();
        } catch (ParseException e) {
            System.err.println("Error parsing number: " + numberStr);
            return 0.0;
        }
    }
    @GetMapping("/technical-analysis")
    @ResponseBody
    public Map<String, Object> getTechnicalAnalysis(
            @RequestParam String issuer,
            @RequestParam String period){
        if(prices == null || !issuer.equals(presentIssuer)){
            initializeTechnicalAnalysisData(issuer);
        }

        int differenceCoefficient = calculateDifferenceCoefficient(period);
        int coefficient = getDateCoefficientTemp(dates.get(dates.size() - 1));
        int counter = dates.size();
        for(int i = dates.size() - 1; i >= 0; i--){
            int rowCoefficient = getDateCoefficientTemp(dates.get(i));
            if(rowCoefficient < coefficient - differenceCoefficient){
                break;
            }
            counter--;
        }

        return Map.of(
                "prices", prices.subList(counter, prices.size()).toArray(),
                "dates", dates.subList(counter, dates.size()).toArray(),
                "buy", buy.subList(counter, buy.size()).toArray(),
                "sell", sell.subList(counter, sell.size()).toArray()
        );
    }

    private void initializeTechnicalAnalysisData(String issuer){
        String result = technicalAnalysisService.analyse(issuer);
        String[] parts = result.split("\\$");
        List<String> partsList = new ArrayList<>();

        for(int i = 0; i < parts.length; i++){
            partsList.add(parts[i]);
        }

        while(partsList.size() != 4)
            partsList.add("10");

        String[] pricesArr = partsList.get(0).split("#");
        String[] datesArr = partsList.get(1).split("#");
        String[] buyArr = partsList.get(2).split("#");
        String[] sellArr = partsList.get(3).split("#");

        prices = new ArrayList<>();
        dates = new ArrayList<>();
        buy = new ArrayList<>();
        sell = new ArrayList<>();

        int j = 0;
        int k = 0;
        for(int i = 0; i < pricesArr.length - 1; i++){
            if(pricesArr[i] == null)
                continue;
            prices.add(Double.parseDouble(pricesArr[i]));

            if(j < buyArr.length && !buyArr[j].isEmpty() && Double.parseDouble(buyArr[j]) == Double.parseDouble(pricesArr[i])){
                buy.add(Double.parseDouble(pricesArr[i]));
                j++;
            }
            else {
                buy.add(null);
            }

            if(k < sellArr.length && !sellArr[k].isEmpty() && Double.parseDouble(sellArr[k]) == Double.parseDouble(pricesArr[i])){
                sell.add(Double.parseDouble(pricesArr[i]));
                k++;
            }
            else {
                sell.add(null);
            }
        }
        for(int i = 0; i < datesArr.length - 1; i++){
            if(datesArr[i] == null)
                continue;

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(datesArr[i].split(" ")[0], inputFormatter);
            String formattedDate = date.format(outputFormatter);
            dates.add(formattedDate);
            System.out.println(dates);
        }
    }
}



