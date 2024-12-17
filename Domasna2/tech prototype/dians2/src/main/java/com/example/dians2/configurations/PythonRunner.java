package com.example.dians2.configurations;

import com.example.dians2.service.CsvImportService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
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
            String scriptPath = "script.py";
            String pythonPath = "C:\\Users\\User\\anaconda3\\python.exe";
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);
            processBuilder.directory(new File("src/main/resources"));
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


            String directoryPath = "src\\main\\resources\\csv";
            csvImportService.importCsvData(directoryPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


