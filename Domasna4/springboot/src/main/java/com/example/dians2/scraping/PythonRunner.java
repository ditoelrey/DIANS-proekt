package com.example.dians2.scraping;

import com.example.dians2.service.impl.CsvImportServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class PythonRunner {

    private final CsvImportServiceImpl csvImportService;

    public PythonRunner(CsvImportServiceImpl csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Async
    public void runPythonScript() {
        try {
            String scriptPath = "/app/src/main/resources/script.py"; // Absolute path in container
            String pythonPath = findPythonInterpreter();
            System.out.println("Using Python interpreter: " + pythonPath);

            // Change working directory to the resources folder in the container
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);
            processBuilder.directory(new File("/app/src/main/resources"));  // Absolute path in container
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

            // Updated absolute path for CSV directory in container
            String directoryPath = "/app/src/main/resources/csv";  // Absolute path for mounted volume
            csvImportService.importCsvData(directoryPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String findPythonInterpreter() {
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
