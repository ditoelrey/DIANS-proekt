package com.example.dians2.service;

import com.example.dians2.model.Issuer;
import com.example.dians2.repository.IssuerDataRepository;
import com.example.dians2.repository.IssuerRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.example.dians2.model.IssuerData;
@Service
public class CsvImportService {

    @Autowired
    private IssuerDataRepository issuerDataRepository;

    @Autowired
    private IssuerRepository issuerRepository;
    @Autowired
    private CodeService codeService;


    public void importCsvData(String directoryPath) {
        try {
            Files.list(Paths.get(directoryPath)).filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    String issuerCode = filePath.getFileName().toString().replace(".csv", "");
                    CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()));
                    String[] nextLine;
                    csvReader.readNext();
                    Issuer issuer = new Issuer();
                    issuer.setIssuerCode(issuerCode);
                    //issuerRepository.save(issuer);
                    Issuer temp=codeService.saveIssuer(issuer);
                    if(temp==null){
                        while ((nextLine = csvReader.readNext()) != null) {
                            IssuerData issuerData = new IssuerData();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
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
                        csvReader.close();



                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvValidationException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public double processAndParseDouble(String value) {
//        String processedValue = value != null && !value.isEmpty() ? value.replace(",", "") : "0.0";
//
//        if (processedValue.matches("^\\d+\\.\\d+$") || processedValue.matches("^\\d+$")) {
//            return Double.parseDouble(processedValue);
//        } else {
//            System.out.println("Invalid number format: " + processedValue);
//            return 0.0;
//        }
//    }
public double processAndParseDouble(String value) {
    String processedValue="0";
    if (value.contains(",") || value.contains(".")) {
        // Replace any commas with empty string
        processedValue= value.replace(",", "");
        // Replace any remaining periods (if used for thousand separator) with empty string
        processedValue = processedValue.replace(".", "");
        return Double.parseDouble(processedValue);
    } else if(value.isEmpty()) {
        return Double.parseDouble(processedValue);
    }
    else{
        // If no separators are present, directly parse the value
        return Double.parseDouble(value);
    }
}


}
