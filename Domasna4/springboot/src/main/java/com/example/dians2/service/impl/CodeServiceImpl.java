package com.example.dians2.service.impl;

import com.example.dians2.model.Issuer;
import com.example.dians2.model.IssuerData;
import com.example.dians2.repository.CodeRepository;
import com.example.dians2.repository.IssuerDataRepository;
import com.example.dians2.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;
    private final IssuerDataRepository issuerDataRepository;
    private static CodeServiceImpl instance;

    public CodeServiceImpl(CodeRepository codeRepository, IssuerDataRepository issuerDataRepository) {
        this.codeRepository = codeRepository;
        this.issuerDataRepository = issuerDataRepository;
    }
    public static synchronized CodeServiceImpl getInstance(CodeRepository codeRepository, IssuerDataRepository issuerDataRepository) {
        if (instance == null) {
            instance = new CodeServiceImpl(codeRepository, issuerDataRepository);
        }
        return instance;
    }

    public List<String> getAllCodes() {
        return codeRepository.findAllByOrderByIssuerCodeAsc().stream().map(Issuer::getIssuerCode).collect(Collectors.toList());
    }

    public Issuer saveIssuer(Issuer issuer) {
        Optional<Issuer> existingIssuer = codeRepository.findByIssuerCode(issuer.getIssuerCode());
        if (existingIssuer.isPresent()) {
            System.out.println("Issuer already exists: " + existingIssuer.get());
            return existingIssuer.get();
        } else {
            Issuer savedIssuer = codeRepository.save(issuer);
            System.out.println("Saved new issuer: " + savedIssuer);
            return null;
        }
    }

    public void saveIssuerData(IssuerData issuerData, Issuer issuer) {
        Issuer managedIssuer = saveIssuer(issuer);
        issuerData.setIssuer(managedIssuer);
        issuerDataRepository.save(issuerData);
    }

}

