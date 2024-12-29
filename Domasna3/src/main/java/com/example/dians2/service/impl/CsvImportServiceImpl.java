package com.example.dians2.service.impl;

import com.example.dians2.model.Issuer;
import com.example.dians2.repository.IssuerDataRepository;
import com.example.dians2.service.CsvImportService;
import com.example.dians2.service.impl.CodeServiceImpl;
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
public class CsvImportServiceImpl implements CsvImportService {

    @Autowired
    private IssuerDataRepository issuerDataRepository;

    @Autowired
    private CodeServiceImpl codeService;
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

    private void saveIssuerData(String[] nextLine, Issuer issuer) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        IssuerData issuerData = new IssuerData();
        issuerData.setDate(dateFormat.parse(nextLine[0]));
        issuerData.setLastTradePrice(nextLine[1]);
        issuerData.setMax(nextLine[2]);
        issuerData.setMin(nextLine[3]);
        issuerData.setAvgPrice(nextLine[4]);
        issuerData.setPercentageChange(nextLine[5]);
        issuerData.setVolume(nextLine[6]);
        issuerData.setTurnoverBest(nextLine[7]);
        issuerData.setTotalTurnover(nextLine[8]);
        issuerData.setIssuer(issuer);
        issuerDataRepository.save(issuerData);
    }

}
