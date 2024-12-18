package com.example.dians2.service;

import com.example.dians2.model.Issuer;
import com.example.dians2.repository.IssuerDataRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.dians2.model.IssuerData;
@Service
public class CsvImportService {

    @Autowired
    private IssuerDataRepository issuerDataRepository;

    @Autowired
    private CodeService codeService;
    public void importCsvData(String directoryPath) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            Files.list(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        executorService.submit(() -> processFile(filePath));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void processFile(Path filePath) {
        try {
            String issuerCode = filePath.getFileName().toString().replace(".csv", "");
            CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()));
            String[] nextLine;
            csvReader.readNext();

            Issuer issuer = new Issuer();
            issuer.setIssuerCode(issuerCode);
            Issuer temp = codeService.saveIssuer(issuer);

            if (temp == null) {
                while ((nextLine = csvReader.readNext()) != null) {
                    saveIssuerData(nextLine, issuer);
                }
            }
            else{
                java.util.Date lastSavedDate = issuerDataRepository.findLatestDateByIssuerId(temp.getId());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                java.util.Date lastCsvDate = null;
                while ((nextLine = csvReader.readNext()) != null) {
                    java.util.Date csvDate = dateFormat.parse(nextLine[0]);
                    if (lastCsvDate == null || csvDate.after(lastCsvDate)) {
                        lastCsvDate = csvDate;
                    }
                }
                System.out.println(lastCsvDate + " najden csv");
                System.out.println(lastSavedDate + "najden baza");


                if (lastSavedDate != null && lastCsvDate != null && lastCsvDate.after(lastSavedDate)) {
                    csvReader.close();
                    csvReader = new CSVReader(new FileReader(filePath.toFile()));
                    csvReader.readNext();

                    while ((nextLine = csvReader.readNext()) != null) {
                        java.util.Date csvDate = dateFormat.parse(nextLine[0]);
                        if (csvDate.after(lastSavedDate)) {
                            saveIssuerData(nextLine, temp);
                        }
                    }
                }

            }
            csvReader.close();
        } catch (IOException | CsvValidationException | ParseException e) {
            e.printStackTrace();
        }
    }

public double processAndParseDouble(String value) {
    String processedValue="0";
    if (value.contains(",") || value.contains(".")) {
        processedValue= value.replace(",", "");
        processedValue = processedValue.replace(".", "");
        return Double.parseDouble(processedValue);
    } else if(value.isEmpty()) {
        return Double.parseDouble(processedValue);
    }
    else{
        return Double.parseDouble(value);
    }
}

    private void saveIssuerData(String[] nextLine, Issuer issuer) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        IssuerData issuerData = new IssuerData();
        issuerData.setDate(dateFormat.parse(nextLine[0]));
        issuerData.setLastTradePrice(processAndParseDouble(nextLine[1]));
        issuerData.setMax(processAndParseDouble(nextLine[2]));
        issuerData.setMin(processAndParseDouble(nextLine[3]));
        issuerData.setAvgPrice(processAndParseDouble(nextLine[4]));
        issuerData.setPercentageChange(processAndParseDouble(nextLine[5]));
        issuerData.setVolume(processAndParseDouble(nextLine[6]));
        issuerData.setTurnoverBest(processAndParseDouble(nextLine[7]));
        issuerData.setTotalTurnover(processAndParseDouble(nextLine[8]));
        issuerData.setIssuer(issuer);
        issuerDataRepository.save(issuerData);
    }


}
