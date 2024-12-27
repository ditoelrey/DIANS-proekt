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
            String pythonPath = findPythonInterpreter();
            System.out.println("Using Python interpreter: " + pythonPath);

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

    private String findPythonInterpreter() {
        String[] commands = {"python3", "python"};
        for (String cmd : commands) {
            try {
                Process process = new ProcessBuilder(cmd, "--version").start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return cmd;
                }
            } catch (Exception ignored) {
            }
        }
        throw new IllegalStateException("Python interpreter not found. Please install Python or set it in the PATH.");
    }
}

