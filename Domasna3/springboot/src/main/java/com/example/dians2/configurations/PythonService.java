package com.example.dians2.configurations;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class PythonService {
    private final PythonRunner pythonRunner;

    public PythonService(PythonRunner pythonRunner) {
        this.pythonRunner = pythonRunner;
    }
    @PostConstruct
    public void runPythonScriptOnStartup() {
        System.out.println("Starting the Python script...");
        pythonRunner.runPythonScript();
    }
}
