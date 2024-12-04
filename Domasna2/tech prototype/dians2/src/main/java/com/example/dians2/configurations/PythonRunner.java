package com.example.dians2.configurations;

import com.example.dians2.service.CsvImportService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class PythonRunner {

    private final CsvImportService csvImportService;

    public PythonRunner(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Async
    public void runPythonScript() {
        try {
            String pythonPath = "C:\\Users\\User\\anaconda3\\python.exe";
            String scriptPath = "C:\\Users\\User\\Documents\\dians\\domashna2DIANS\\dians2\\src\\main\\java\\com\\example\\dians2\\configurations\\script.py";

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script executed with exit code: " + exitCode);

            String directoryPath = "C:\\Users\\User\\Documents\\dians\\domashna2DIANS\\dians2\\csv";
            csvImportService.importCsvData(directoryPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

